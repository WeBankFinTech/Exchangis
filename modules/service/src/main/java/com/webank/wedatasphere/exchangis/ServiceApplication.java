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
import com.webank.wedatasphere.exchangis.route.MachineLoadProviderConfig;
import com.webank.wedatasphere.exchangis.route.feign.FeginClientConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableFeignClients(defaultConfiguration = {FeginClientConfig.class})
@MapperScan("com.webank.wedatasphere.exchangis.*.dao")
@RibbonClients(value = {@RibbonClient(name = "exchangis-executor", configuration = MachineLoadProviderConfig.class)})
public class ServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceApplication.class);
    public static void main(String[] args) {
        String pidFile = System.getProperty("pid.file", "");
        try {
            ProcessUtil.mountPIDFile(pidFile);
        }catch(Exception e){
            LOG.error("Fail to store PID file in disk path: [" + pidFile +"]", e);
            return;
        }
        try {
            SpringApplication.run(ServiceApplication.class, args);
        }catch(Throwable e){
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    @Bean
    MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
        return meterRegistry1 -> {
            meterRegistry.config()
                    .commonTags("application", "Tenantapp");
            //所有指标添加统一标签： application = Tenantapp
        };
    }
}
