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

package com.webank.wedatasphere.exchangis.scheduler.cluster.bean;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.exec.remote.ExecuteService;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.alarm.AlarmTemplate;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.dao.JobTaskDao;
import com.webank.wedatasphere.exchangis.job.domain.JobTask;
import com.webank.wedatasphere.exchangis.job.service.impl.JobInfoServiceImpl;
import com.webank.wedatasphere.exchangis.queue.dao.ElementDao;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;
import com.webank.wedatasphere.exchangis.scheduler.cluster.SchedulerClusterConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Collections;
import java.util.List;

/**
 * @author davidhua
 * 2019/4/18
 */
public class TaskQueueRepairBean extends QuartzJobBean {
    private static final Logger LOG = LoggerFactory.getLogger(TaskQueueRepairBean.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try{
            JobDataMap payloadData = context.getJobDetail().getJobDataMap();
            int threshold = payloadData.getIntValue(SchedulerClusterConstants.REPAIR_POLL_THRESHOLD);
            //Search from db directly
            ElementDao elementDao = AppUtil.getBean(ElementDao.class);
            LOG.info("Task queue repair threshold [{}]", threshold);
            List<QueueElement> elements = elementDao.selectWaitForComplete(threshold);
            if(!elements.isEmpty()){
                EurekaDiscoveryClient client = AppUtil.getBean(EurekaDiscoveryClient.class);
                ExecuteService executeService = AppUtil.getBean(ExecuteService.class);
                JobTaskDao jobTaskDao = AppUtil.getBean(JobTaskDao.class);
                JobInfoServiceImpl jobInfoService = AppUtil.getBean(JobInfoServiceImpl.class);
                elements.forEach((element) ->{
                    try {
                        LOG.info("Repair queue element {}", Json.toJson(element, null));
                        List<ServiceInstance> instances = client.getInstances(JobConstants.EXECUTOR_NAME);
                        JobTask jobTask = jobTaskDao.selectOne(element.getId());
                        if (null != jobTask && jobTask.getStatus().equalsIgnoreCase(ExecuteStatus.COMMIT.name())) {
                            boolean exists = false;
                            String hostPort = null;
                            for (ServiceInstance instance : instances) {
                                hostPort = instance.getHost() + ":" + instance.getPort();
                                Response<Boolean> response = executeService.alive(hostPort, jobTask.getJobId(), jobTask.getId());
                                if(response.getData() != null){
                                    exists = response.getData();
                                }
                                if(exists){
                                    break;
                                }
                            }
                            if(jobTask.isDisposable()){
                                jobInfoService.delete(Collections.singletonList(jobTask.getJobId()));
                            }
                            if(exists){
                                jobTask.setStatus(ExecuteStatus.RUNNING.name());
                                jobTask.setExecuterAddress(hostPort);
                                if(jobTaskDao.update(jobTask) > 0){
                                    LOG.info("Change task [{}] status to RUNNING", jobTask.getId());
                                }
                            }else{
                                jobTask.setStatus(ExecuteStatus.FAILD.name());
                                if(jobTaskDao.update(jobTask) > 0){
                                    LOG.info("Change task [{}] status to FAILD", jobTask.getId());
                                    jobInfoService.sendInfoToAlarm(jobTask.getId(),
                                            jobTask.getJobId(), AlarmTemplate.TITLE_TASK_RUN_FAID,
                                            PatternInjectUtils.inject(AlarmTemplate.TASK_RUN_FAID,
                                                    new Object[]{jobTask.getJobName(), jobTask.getJobId(), jobTask.getId()}, false, false, true));
                                    elementDao.delete(Collections.singletonList(element.getId()));
                                }
                            }

                        } else if (null == jobTask) {
                            LOG.info("Delete duplicate element {}", Json.toJson(element, null));
                            elementDao.delete(Collections.singletonList(element.getId()));
                        } else if (jobTask.getStatus().equalsIgnoreCase(ExecuteStatus.FAILD.name()) ||
                                jobTask.getStatus().equalsIgnoreCase(ExecuteStatus.SUCCESS.name())){
                            LOG.info("Delete duplicate element {}", Json.toJson(element, null));
                            elementDao.delete(Collections.singletonList(element.getId()));
                            if(jobTask.isDisposable()){
                                jobInfoService.delete(Collections.singletonList(jobTask.getJobId()));
                            }
                        }
                    }catch(Exception e){
                        LOG.error("Repair queue element {} failed , message: {}",
                                Json.toJson(element, null) , e.getMessage());
                    }
                });
            }
        }catch(Exception e){
            LOG.error(e.getMessage());
        }
    }
}
