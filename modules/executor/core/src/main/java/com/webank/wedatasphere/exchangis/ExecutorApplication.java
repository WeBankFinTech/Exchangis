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
import com.webank.wedatasphere.exchangis.route.feign.FeginClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by devendeng on 2018/8/28.
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, JestAutoConfiguration.class, ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = {FeginClientConfig.class})
public class ExecutorApplication {
    private static Logger LOG = LoggerFactory.getLogger(ExecutorApplication.class);

    public static void main(String[] args) {
        String pidFile = System.getProperty("pid.file", "");
        try {
            ProcessUtil.mountPIDFile(pidFile);
        }catch(Exception e){
            LOG.error("Fail to store PID file in disk path: [" + pidFile +"]", e);
            return;
        }
        SpringApplication.run(ExecutorApplication.class, args);
    }
}
