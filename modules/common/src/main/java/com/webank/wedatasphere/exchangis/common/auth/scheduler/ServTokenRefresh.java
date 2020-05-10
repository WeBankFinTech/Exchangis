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

package com.webank.wedatasphere.exchangis.common.auth.scheduler;

import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author davidhua
 * 2018/11/4
 */
@Component
public class ServTokenRefresh {
    private static final Logger LOG = LoggerFactory.getLogger(ServTokenRefresh.class);
    private static final String TOKEN_NO_STORE_FLAG = "NO_STORE";
    @Resource
    private AuthConfiguration authConfiguration;

    @Autowired
    private AuthTokenService authTokenService;
    @PostConstruct
    public void run(){
        LOG.info("switch status: " + authConfiguration.enable());
        if(authConfiguration.enable()) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            Runnable runnable = () -> {
                try {
                    refresh();
                } catch (Exception e) {
                    LOG.error("Refresh method happened error", e);
                }
            };
            service.scheduleAtFixedRate(runnable, 0,
                    authConfiguration.authTokenServRefreshInterval(), TimeUnit.SECONDS);
        }
    }

    public void refresh(){
        try{
            String body = authTokenService.refreshServerToken(authConfiguration.authTokenServRefreshId(),
                    authConfiguration.authTokenServRefreshPwd());
            if(StringUtils.isNotBlank(body)){
                System.setProperty(AuthConstraints.ENV_SERV_TOKEN, body);
                LOG.info("Get Token:" + body.substring(0, 12) + "*******");
                String storePath = authConfiguration.authTokenServRefreshStore();
                if(StringUtils.isNotBlank(storePath) && !storePath.equals(TOKEN_NO_STORE_FLAG)){
                    storeToken(body, storePath);
                    System.setProperty(AuthConstraints.ENV_SERV_TOKEN_PATH, storePath);
                }
            }
        }catch(Exception e){
            LOG.info(e.getMessage());
        }
    }
    private void storeToken(String token, String storePath){
        File storeFile = new File(storePath);
        if(storeFile.getParentFile().exists() || storeFile.getParentFile().mkdirs()) {
            try (BufferedWriter writer = new BufferedWriter(new
                    OutputStreamWriter(new FileOutputStream(storeFile, false)))) {
                writer.append(token);
                writer.flush();
            } catch (IOException e) {
                LOG.info("Store Token into File error, Exception:{}", e.getMessage());
            }
        }
    }
}
