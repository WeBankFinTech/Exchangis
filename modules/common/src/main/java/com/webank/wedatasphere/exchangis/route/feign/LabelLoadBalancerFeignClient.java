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

package com.webank.wedatasphere.exchangis.route.feign;

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.route.exception.RpcReqTimeoutException;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import feign.Client;
import feign.Request;
import feign.Response;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.webank.wedatasphere.exchangis.route.feign.FeignConstants.HOST_PORT;

/**
 * @author davidhua
 * 2018/9/12
 */
public class LabelLoadBalancerFeignClient implements Client{

    static final Request.Options DEFAULT_OPTIONS = new Request.Options();

    private final Client delegate;
    private LabelLoadBalancerFactory lbClientFactory;
    private SpringClientFactory clientFactory;

    LabelLoadBalancerFeignClient(Client delegate, LabelLoadBalancerFactory lbClientFactory, SpringClientFactory clientFactory) {
        this.delegate = delegate;
        this.lbClientFactory = lbClientFactory;
        this.clientFactory = clientFactory;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        //When the request has the 'Host-Port' header
        //use it instead of balancing, the header can be modified dynamically
        Map<String, Collection<String>> headers = new LinkedHashMap<>(
                request.headers());
        Collection<String> hostPorts = headers.getOrDefault(HOST_PORT, Collections.emptyList());
        //Put token which wrapped by sso cookie into request headers
        String clientName = URI.create(request.url()).getHost();
        if(StringUtils.isNotBlank(System.getProperty(AuthConstraints.ENV_SERV_TOKEN)) &&
                 !clientName.equals(AuthConstraints.GATEWAY_CLIENT)){
            headers.put("Cookie",
                    Collections.singleton(AuthConstraints.DEFAULT_SSO_COOKIE + "="
                            + System.getProperty(AuthConstraints.ENV_SERV_TOKEN)));
        }
        headers.put("X-Requested-With", Collections.singleton("XMLHttpRequest"));
        IClientConfig requestConfig = getClientConfig(options, clientName);
        URI uri;
        boolean withLoadBalance = true;
        if(!hostPorts.isEmpty()){
            String hostPort = hostPorts.iterator().next();
            uri = replaceHost(request.url(), clientName, hostPort);
            withLoadBalance = false;
        }else{
            uri = replaceHost(request.url(), clientName, "");
        }
        LabelLoadBalancerFactory.FeignLabelLoadBalancer.LbClientRequest
                lbClientRequest = new LabelLoadBalancerFactory.FeignLabelLoadBalancer.LbClientRequest(
                this.delegate, request, headers, uri);
        LabelLoadBalancerFactory.FeignLabelLoadBalancer lb = this.lbClientFactory.create(clientName);
        try {
            return withLoadBalance ? lb.executeWithLoadBalancer(lbClientRequest, requestConfig).toResponse()
            : lb.execute(lbClientRequest, requestConfig).toResponse();
        }catch(Exception e){
            Throwable timeout = findTimeoutException(e);
            if(null != timeout) {
                Server server = lbClientRequest.getChosenServer();
                throw new RpcReqTimeoutException(null == server?"" : server.getHostPort(), e);
            }
            IOException io = findIOException(e);
            if(null != io){
                throw io;
            }
            throw new RuntimeException(e);
        }
    }

    private IOException findIOException(Throwable throwable){
        if(throwable == null){
            return null;
        }
        if (throwable instanceof IOException){
            return (IOException)throwable;
        }
        return findIOException(throwable.getCause());
    }

    private Throwable findTimeoutException(Throwable throwable){
        Throwable cause = throwable;
        while(null != cause){
            if(cause instanceof TimeoutException || cause instanceof SocketTimeoutException){
                return cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
    private IClientConfig getClientConfig(Request.Options options, String clientName){
        IClientConfig requestConfig;
        if(options == DEFAULT_OPTIONS){
            requestConfig = this.clientFactory.getClientConfig(clientName);
        }else{
            requestConfig = new DefaultClientConfigImpl();
            requestConfig.set(CommonClientConfigKey.ConnectTimeout,
                    options.connectTimeoutMillis());
            requestConfig.set(CommonClientConfigKey.ReadTimeout, options.readTimeoutMillis());
        }
        return requestConfig;
    }

    private static URI replaceHost(String originalUrl, String clientName, String host){
        return URI.create(originalUrl.replaceFirst(clientName, host));
    }
}
