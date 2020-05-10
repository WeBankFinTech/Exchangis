/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.gateway.auth;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenBean;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.common.auth.AuthConstraints.DEFAULT_SSO_COOKIE;

/**
 * Entrance filter:
 * 1)Log request information
 * 2)Intercept the login request and modify the response entity
 * 3)Intercept the request of refreshing token
 * @author davidhua
 * 2020/4/2
 */
public class AuthEntranceFilter implements GlobalFilter, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(AuthEntranceFilter.class);
    @Value("${sso.loginUrl}")
    private String loginUrl;

    @Value("${server.port}")
    private Integer serverPort;

    @Resource
    private AuthTokenHelper tokenBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOG.info(Json.toJson(exchange.getRequest(), null));
        if(exchange.getRequest().getPath().value().equals(loginUrl)) {
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
                @Override
                @SuppressWarnings("unchecked")
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            if(getStatusCode() == HttpStatus.OK) {
                                String jsonStr = new String(content, StandardCharsets.UTF_8);
                                try {
                                    Response<Map<String, String>> response = Json.fromJson(jsonStr, Response.class, Map.class);
                                    assert response != null;
                                    if(response.getCode() == 0) {
                                        Map<String, String> jsonMap = response.getData();
                                        String loginId = jsonMap.getOrDefault(AuthConstraints.X_AUTH_ID, "");
                                        if (StringUtils.isNotBlank(loginId)) {
                                            AuthTokenBean tokenBean = new AuthTokenBean();
                                            tokenBean.getHeaders().put(AuthConstraints.X_AUTH_ID, loginId);
                                            tokenBean.getHeaders().put(AuthConstraints.HOST_PORT,
                                                    String.format("%s:%s", InetAddress.getLocalHost().getHostAddress() , serverPort));
                                            tokenBean.getClaims().putAll(jsonMap);
                                            String token = tokenBuilder.build(tokenBean);
                                            addCookie(ResponseCookie.from(DEFAULT_SSO_COOKIE, token)
                                                    .path("/").httpOnly(true).secure(false)
                                                    .maxAge(Duration.ofDays(1)).build());
                                            LOG.info("Add token: " + token.substring(0, 6) + "**** to login response");
                                        }
                                    }
                                } catch (Exception e) {
                                    LOG.error("Generate Token Fail: [" + e.getMessage() +"]", e);
                                    //Ignore
                                }
                            }
                            //Just trainst data
                            return bufferFactory.wrap(content);
                        }));
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(responseDecorator).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

}
