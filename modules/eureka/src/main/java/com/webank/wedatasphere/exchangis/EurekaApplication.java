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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author davidhua
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    private static Logger LOG = LoggerFactory.getLogger(EurekaApplication.class);

    public static void main( String[] args ) {
        if(StringUtils.isNotBlank(System.getProperty("pid.file", ""))){
            String pidFile = System.getProperty("pid.file");
            try {
                savePID(pidFile);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        removePID(pidFile);
                    } catch (IOException e) {
                        //Ignore
                    }
                }));
            }catch(IOException e){
                LOG.error("Fail to store PID file in disk path: [" + pidFile +"]", e);
                return;
            }
        };
        SpringApplication.run(EurekaApplication.class, args);
    }

    private static void savePID(String pidFile) throws IOException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0].trim();
        Files.write(Paths.get(pidFile), pid.getBytes());
    }

    private static void removePID(String pidFile) throws IOException{
        Files.delete(Paths.get(pidFile));
    }
}
