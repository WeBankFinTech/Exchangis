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
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeDao;
import com.webank.wedatasphere.exchangis.job.dao.JobExecNodeDao;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.route.exception.NoAvailableServerException;
import com.webank.wedatasphere.exchangis.route.feign.FeignConstants;
import com.google.common.util.concurrent.AtomicDouble;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by devendeng on 2018/9/4.
 */

public class MachineLoadRule extends AbstractLoadBalancerRule {

    private static final Logger logger = LoggerFactory.getLogger(MachineLoadRule.class);

    private MachineLoadConf machineLoadConf;

    private ExecNodeDao nodeDao;

    private JobExecNodeDao jobExecNodeDao;

    private UserExecNodeDao userExecNodeDao;

    private SecureRandom random = new SecureRandom();
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }

    private Server choose(ILoadBalancer lb, Object key){
        Server server = null;
        if(null != lb){
            while(null == server && !Thread.interrupted()){
                List<Server> upList = lb.getReachableServers();
                List<Server> allList = lb.getAllServers();
                if(allList.size() == 0){
                    return null;
                }
                List<String> hostPorts = new ArrayList<>();
                upList.forEach(s -> {
                    if(s.isAlive()) {
                        hostPorts.add(s.getHostPort());
                    }
                });
                logger.trace("UpList:"+ Json.toJson(upList, String.class));
                List<ExecutorNode> candidates = new ArrayList<>();
                if(null != key){
                    String keyStr = String.valueOf(key);
                    if(keyStr.startsWith(FeignConstants.LB_LABEL_PREFIX_JOB)) {
                        String jobIdStr = keyStr.substring(FeignConstants.LB_LABEL_PREFIX_JOB.length());
                        if(StringUtils.isNotBlank(jobIdStr)){
                            long jobId = Long.valueOf(jobIdStr);
                            candidates = jobExecNodeDao.getAvailsByJobId(jobId, machineLoadConf.getHeartBeatAvailInterval());
                        }
                    }else if(keyStr.startsWith(FeignConstants.LB_LABEL_PREFIX_USER)){
                        String userName = keyStr.substring(FeignConstants.LB_LABEL_PREFIX_USER.length());
                        if(StringUtils.isNotBlank(userName)){
                            candidates = userExecNodeDao.getAvailNodesByUser(userName, machineLoadConf.getHeartBeatAvailInterval());
                        }
                    }else if(keyStr.startsWith(FeignConstants.LB_LABEL_PREFIX_TAB)){
                        //do nothing
                    }
                }else {
                    candidates = nodeDao.getAvails(machineLoadConf.getHeartBeatAvailInterval());
                }
                //retain all in hostPorts list
                candidates.retainAll(hostPorts);
                logger.info("Candidate nodes:" + Json.toJson(candidates, null));
                if(candidates.isEmpty()){
                    throw new NoAvailableServerException("No available candidate servers");
                }
                int cpuW = machineLoadConf.getCpuWeight();
                int memW = machineLoadConf.getMemWeight();
                double sum = (double)cpuW + (double)memW;
                AtomicDouble count = new AtomicDouble();
                final List<Double> nodeWeight = new ArrayList<>();
                Iterator<ExecutorNode> iterator = candidates.iterator();
                while(iterator.hasNext()){
                    ExecutorNode e = iterator.next();
                    if (e.getCpuRate() >= machineLoadConf.getCpuThreshold() ||
                            e.getMemRate() >= machineLoadConf.getMemThreshold()) {
                        iterator.remove();
                        continue;
                    }
                    double v =((1.0 - e.getCpuRate()) * cpuW + (1.0 - e.getMemRate()) * memW) /sum;
                    count.addAndGet(v);
                    nodeWeight.add(v);
                }
                if(candidates.size() <= 0){
                    throw new NoAvailableServerException("No available candidate servers");
                }
                double v = random.nextDouble() * count.get();
                int select = 0;
                double pos = 0.0;
                for(;select < nodeWeight.size(); select++){
                    pos += nodeWeight.get(select);
                    if(pos >= v){
                        break;
                    }
                }
                select = hostPorts.indexOf(candidates.get(select).getAddress());
                if(select >= 0){
                    server = upList.get(select);
                }
            }
        }
        logger.info("Finally choose server:[" + server+"]");
        return server;
    }

    void setNodeDao(ExecNodeDao nodeDao){
        this.nodeDao = nodeDao;
    }

    void setJobExecNodeDao(JobExecNodeDao jobExecNodeDao){
        this.jobExecNodeDao = jobExecNodeDao;
    }
    void setMachineLoadConf(MachineLoadConf conf){
        this.machineLoadConf = conf;
    }

    public void setUserExecNodeDao(UserExecNodeDao userExecNodeDao) {
        this.userExecNodeDao = userExecNodeDao;
    }
}
