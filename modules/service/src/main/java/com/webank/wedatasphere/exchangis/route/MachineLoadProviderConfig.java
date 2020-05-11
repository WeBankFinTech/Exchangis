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

package com.webank.wedatasphere.exchangis.route;

import com.webank.wedatasphere.exchangis.auth.dao.UserExecNodeDao;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeDao;
import com.webank.wedatasphere.exchangis.job.dao.JobExecNodeDao;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Created by devendeng on 2018/9/4.
 */

public class MachineLoadProviderConfig {
    private static Logger logger = LoggerFactory.getLogger(MachineLoadProviderConfig.class);
    @Bean
    public IRule ribbonRule(IClientConfig config, ExecNodeDao nodeDao, JobExecNodeDao jobExecNodeDao,
                            MachineLoadConf conf, UserExecNodeDao userExecNodeDao) {
        logger.info("Use MachineLoadRole config.");
        MachineLoadRule loadRule = new MachineLoadRule();
        loadRule.initWithNiwsConfig(config);
        loadRule.setNodeDao(nodeDao);
        loadRule.setMachineLoadConf(conf);
        loadRule.setJobExecNodeDao(jobExecNodeDao);
        loadRule.setUserExecNodeDao(userExecNodeDao);
        return loadRule;
    }
}
