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

import com.netflix.client.*;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import feign.Client;
import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.RibbonProperties;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.util.ConcurrentReferenceHashMap;
import rx.Observable;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import static com.webank.wedatasphere.exchangis.route.feign.FeignConstants.LB_LABEL;
import static org.springframework.cloud.netflix.ribbon.RibbonUtils.updateToSecureConnectionIfNeeded;

/**
 * The factory of load-balancer which with server key(label)
 * @author davidhua
 * 2019/3/19
 */
public class LabelLoadBalancerFactory {
    private final SpringClientFactory factory;
    private Map<String, FeignLabelLoadBalancer> cache =
                new ConcurrentReferenceHashMap<>();

    public LabelLoadBalancerFactory(SpringClientFactory factory){
        this.factory = factory;
    }

    public FeignLabelLoadBalancer create(String clientName){
        return this.cache.computeIfAbsent(clientName, (key)->{
            IClientConfig config = this.factory.getClientConfig(clientName);
            ILoadBalancer lb = this.factory.getLoadBalancer(clientName);
            ServerIntrospector serverIntrospector = this.factory.getInstance(clientName, ServerIntrospector.class);
            return new FeignLabelLoadBalancer(lb, config, serverIntrospector);
        });
    }
    public static class FeignLabelLoadBalancer extends
            AbstractLoadBalancerAwareClient<FeignLabelLoadBalancer.LbClientRequest, FeignLabelLoadBalancer.LbClientResponse> {
        private static final Logger logger = LoggerFactory.getLogger(FeignLabelLoadBalancer.class);
        private static final int DEFAULT_CONNECT_TIMEOUT = 3000;
        private static final int DEFAULT_READ_TIMEOUT = 10000;

        private int connectTimeout;
        private int readTimeout;
        private RibbonProperties ribbon;
        private IClientConfig clientConfig;
        private ServerIntrospector serverIntrospector;

        public FeignLabelLoadBalancer(ILoadBalancer lb) {
            super(lb);
            this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            this.readTimeout = DEFAULT_READ_TIMEOUT;
        }

         FeignLabelLoadBalancer(ILoadBalancer lb, IClientConfig clientConfig,
                                      ServerIntrospector serverIntrospector) {
            super(lb, clientConfig);
            this.setRetryHandler(RetryHandler.DEFAULT);
            this.clientConfig = clientConfig;
            RibbonProperties ribbon= RibbonProperties.from(clientConfig);
            this.ribbon = ribbon;
            this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            this.readTimeout = DEFAULT_READ_TIMEOUT;
            this.serverIntrospector = serverIntrospector;
        }


        @Override
        public RequestSpecificRetryHandler getRequestSpecificRetryHandler(LbClientRequest request, IClientConfig requestConfig) {
            if(null != this.ribbon && this.ribbon.isOkToRetryOnAllOperations()){
                return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
                        requestConfig);
            }
            if(!"GET".equals(request.toRequest().method())){
                return new RequestSpecificRetryHandler(true, false, this.getRetryHandler(),
                        requestConfig);
            }else{
                return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
                        requestConfig);
            }
        }

        @Override
        protected void customizeLoadBalancerCommandBuilder(LbClientRequest request, IClientConfig config, LoadBalancerCommand.Builder<LbClientResponse> builder) {
            //set load balancer key
            Object key = request.getLoadBalancerKey();
            if(null != key){
                logger.info("set load balancer label:" + request.getLoadBalancerKey());
                builder.withServerLocator(request.getLoadBalancerKey());
            }
        }

        @Override
        public URI reconstructURIWithServer(Server server, URI original) {
            URI uri = updateToSecureConnectionIfNeeded(original, this.clientConfig, this.serverIntrospector, server);
            return super.reconstructURIWithServer(server, uri);
        }


        @Override
        public LbClientResponse execute(LbClientRequest lbClientRequest, IClientConfig iClientConfig) throws Exception {
            Request.Options options;
            if(null != iClientConfig){
                RibbonProperties override = RibbonProperties.from(iClientConfig);
                options = new Request.Options(this.connectTimeout, this.readTimeout);
            }else{
                options = new Request.Options(this.connectTimeout, this.readTimeout);
            }
            return new LbClientResponse(lbClientRequest.getUri(),
                    lbClientRequest.client.execute(lbClientRequest.toRequest(), options));
        }

        @Override
        public LbClientResponse executeWithLoadBalancer(LbClientRequest request, IClientConfig requestConfig) throws ClientException {
            LoadBalancerCommand<LbClientResponse> command = buildLoadBalancerCommand(request, requestConfig);

            try {
                return command.submit(
                        server -> {
                            //set the server chosen
                            request.chosenServer = server;
                            URI finalUri = reconstructURIWithServer(server, request.getUri());
                            LbClientRequest requestForServer = (LbClientRequest) request.replaceUri(finalUri);
                            try {
                                return Observable.just(this.execute(requestForServer, requestConfig));
                            }
                            catch (Exception e) {
                                return Observable.error(e);
                            }
                        })
                        .toBlocking()
                        .single();
            } catch (Exception e) {
                Throwable t = e.getCause();
                if (t instanceof ClientException) {
                    throw (ClientException) t;
                } else {
                    throw new ClientException(e);
                }
            }
        }

        static class LbClientRequest extends ClientRequest{
            private final Request request;
            private final Client client;
            private Map<String, Collection<String>> headers;
            private Server chosenServer;
            LbClientRequest(Client client, Request request,
                            Map<String, Collection<String>> headers, URI uri){
                this.client = client;
                setUri(uri);
                this.headers = headers;
                if(null != headers && !headers.getOrDefault(LB_LABEL, Collections.emptyList()).isEmpty()){
                    setLoadBalancerKey(headers.get(LB_LABEL).toArray()[0]);
                }
                this.request = toRequest(request);
            }

            private Request toRequest(Request request){
                return Request.create(request.method(), getUri().toASCIIString(), this.headers, request.body(), request.charset());
            }

            Request toRequest(){
                return toRequest(this.request);
            }

            public Server getChosenServer() {
                return chosenServer;
            }

        }

        static class LbClientResponse implements IResponse{

            private final URI uri;
            private final Response response;
            LbClientResponse(URI uri,  Response response){
                this.uri = uri;
                this.response = response;
            }
            @Override
            public Object getPayload() throws ClientException {
                return this.response.body();
            }

            @Override
            public boolean hasPayload() {
                return this.response.body() != null;
            }

            @Override
            public boolean isSuccess() {
                return this.response.status() == 200;
            }

            @Override
            public URI getRequestedURI() {
                return this.uri;
            }

            @Override
            public Map<String, ?> getHeaders() {
                return this.response.headers();
            }

            @Override
            public void close() throws IOException {
                if(this.response != null && this.response.body() != null){
                    this.response.body().close();
                }
            }

            Response toResponse(){
                return this.response;
            }
        }
    }
}
