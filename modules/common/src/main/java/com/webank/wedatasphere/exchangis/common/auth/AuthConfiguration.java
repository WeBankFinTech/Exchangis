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

package com.webank.wedatasphere.exchangis.common.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author davidhua
 * 2018/10/17
 */
@Component
@PropertySource(value="classpath:auth.properties",ignoreResourceNotFound = true)
public class AuthConfiguration {
    @Bean
    public AuthTokenHelper tokenBuilder(@Value("${auth.token.api:token}")String tokenApi){
        return new AuthTokenHelper(tokenApi);
    }
    @Value("${auth.switch:false}")
    private boolean authSwitch;

    @Value("${auth.secret:secret}")
    private String authSecret;

    @Value("${auth.gateway.url:http}")
    private String authGatewayUrl;

    @Value("${auth.cas.switch:false}")
    private boolean authCasSwitch;

    @Value("${auth.cas.systemId:id}")
    private String authCasSystemId;

    @Value("${auth.token.invalid:0}")
    private long authTokenInvalid;
    /**
     * refresh url for token which generated for service
     */
    @Value("${auth.token.servRefresh.url:http}")
    private String authTokenServRefreshUrl;

    @Value("${auth.token.servRefresh.interval:0}")
    private long authTokenServRefreshInterval;

    @Value("${auth.token.servRefresh.id:id}")
    private String authTokenServRefreshId;

    @Value("${auth.token.servRefresh.pwd:pwd}")
    private String authTokenServRefreshPwd;

    @Value("${auth.token.servRefresh.store:NO_STORE}")
    private String authTokenServRefreshStore;

    @Value("${auth.login.url:/udes/auth/login}")
    private String authLoginUrl;

    public boolean enable(){
        return authSwitch;
    }

    public boolean authCasSwitch(){
        return authCasSwitch;
    }

    public String gatewayUrl(){
        return authGatewayUrl;
    }

    public String casSystemId(){
        return authCasSystemId;
    }

    public long tokenInvalid(){
        return authTokenInvalid;
    }

    public String authTokenServRefreshUrl() {
        return authTokenServRefreshUrl;
    }

    public long authTokenServRefreshInterval() {
        return authTokenServRefreshInterval;
    }

    public String authTokenServRefreshId(){
        return authTokenServRefreshId;
    }

    public String authTokenServRefreshPwd(){
        return authTokenServRefreshPwd;
    }

    public String authTokenServRefreshStore(){
        return authTokenServRefreshStore;
    }

    public String authLoginUrl(){
        return authLoginUrl;
    }

    public String authSecret(){
        return authSecret;
    }
}

