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

package com.webank.wedatasphere.exchangis;

import com.webank.wedatasphere.exchangis.common.util.ProcessUtil;
import com.webank.wedatasphere.exchangis.gateway.auth.AuthEntranceFilter;
import com.webank.wedatasphere.exchangis.gateway.auth.pwd.filters.ScriptAuthPwdFilter;
import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenService;
import com.webank.wedatasphere.exchangis.common.auth.scheduler.ServTokenRefresh;
import com.webank.wedatasphere.exchangis.common.controller.SecurityUtil;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.gateway.filters.CorsGatewayFilter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * @author davidhua
 * 2018/9/21
 */
@SpringBootApplication
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes=
                        {
                                AppUtil.class,
                                AuthConfiguration.class,
                                ServTokenRefresh.class,
                                AuthTokenService.class,
                                SecurityUtil.class
                        })
        }
)
@Controller
public class WebApplication{

    private static Logger LOG = LoggerFactory.getLogger(WebApplication.class);

    @Bean
    public AuthTokenHelper tokenBuilder(@Value("${auth.token.secret}")String tokenSecret){
        return new AuthTokenHelper(tokenSecret);
    }
    @Bean
    public AuthEntranceFilter authLoginFilter(){return new AuthEntranceFilter();}

    @Bean
    public ScriptAuthPwdFilter scriptAuthPwdFilter(){return new ScriptAuthPwdFilter();}


    @Bean
    public CorsGatewayFilter corsHeadersFilter(){
        return new CorsGatewayFilter();
    }

    @Value("${web.server.path}")
    private String serverPath;

    @GetMapping(value= {"/", "/api/v1"})
    public ResponseEntity<InputStreamResource> index() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html;charset=utf-8");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(new ClassPathResource(serverPath).getInputStream()));
    }

    public static void main(String[] args) {
        String pidFile = System.getProperty("pid.file", "");
        try {
            ProcessUtil.mountPIDFile(pidFile);
        }catch(Exception e){
            LOG.error("Fail to store PID file in disk path: [" + pidFile +"]", e);
            return;
        }
        SpringApplication.run(WebApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
        return meterRegistry1 -> {
            meterRegistry.config()
                    .commonTags("application", "WebApplication");
            //所有指标添加统一标签： application = Tenantapp
        };
    }
}
