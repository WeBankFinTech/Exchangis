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

package com.webank.wedatasphere.exchangis.gateway.auth.pwd.filters;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenBean;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.util.AESUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Objects;

/**
 * @author davidhua
 * 2018/11/12
 */
public class ScriptAuthPwdFilter implements WebFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ScriptAuthPwdFilter.class);
    @Resource
    private AuthTokenHelper tokenBuilder;

    @Value("${auth.script.secret-key}")
    private String secretKeyPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(headers.containsKey(AuthConstraints.SCRIPT_ACCOUNT_NAME) &&
                headers.containsKey(AuthConstraints.SCRIPT_PWD_NAME)){
            String userName = String.valueOf(Objects.requireNonNull(headers.get(AuthConstraints.SCRIPT_ACCOUNT_NAME)).get(0));
            String pwd = String.valueOf(Objects.requireNonNull(headers.get(AuthConstraints.SCRIPT_PWD_NAME)).get(0));
            LOG.info("start to validate script auth, user:" + userName +", pwd:*****");
            try {
                String keyContent = FileCopyUtils.
                        copyToString(new BufferedReader(new InputStreamReader(new FileInputStream(secretKeyPath))));
                if(StringUtils.isNotBlank(keyContent)){
                    pwd = AESUtils.decrypt(pwd, Base64.getDecoder().decode(keyContent));
                }
                String umPwd = pwd.substring(userName.length());
                //TODO Use Feign to do login
                boolean loginResult = true;
                if(loginResult) {
                    AuthTokenBean tokenBean = new AuthTokenBean();
                    tokenBean.getHeaders().put(AuthConstraints.X_AUTH_ID, userName);
                    String token = tokenBuilder.build(tokenBean);
                    ServerHttpRequest request = exchange.getRequest()
                            .mutate().header("Cookie", AuthConstraints.DEFAULT_SSO_COOKIE + "=" + token)
                            .header("X-Requested-With", "XMLHttpRequest").build();
                    LOG.info("Add sso cookie to script request");
                    return chain.filter(exchange.mutate().request(request).build());
                }
            }catch(Exception e){
                LOG.info("Basic authenticate failed in script request, username: " + userName + ", message: " + e.getMessage(), e);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

}
