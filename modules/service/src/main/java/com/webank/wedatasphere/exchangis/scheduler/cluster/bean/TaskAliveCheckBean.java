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

import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.alarm.AlarmTemplate;
import com.webank.wedatasphere.exchangis.job.domain.JobTask;
import com.webank.wedatasphere.exchangis.job.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.service.JobTaskAliveCallback;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.scheduler.cluster.SchedulerClusterConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * check if the task is alive
 * @author davidhua
 * 2019/2/13
 */
public class TaskAliveCheckBean extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(TaskAliveCheckBean.class);
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap payloadData = context.getJobDetail().getJobDataMap();
            int checkNum = payloadData.getIntValue(SchedulerClusterConstants.ALIVE_CHECK_NUM);
            JobTaskService taskService = AppUtil.getBean(JobTaskService.class);
            JobInfoService jobInfoService = AppUtil.getBean(JobInfoService.class);
            taskService.checkAliveInBatch(checkNum, new JobTaskAliveCallback() {
                @Override
                public void alive(JobTask jobTask) {
                    logger.info("Task jobId: [{}], taskId: [{}] is still alive", jobTask.getJobId(), jobTask.getId());
                }

                @Override
                public void notAlive(JobTask jobTask) {
                    logger.info("Task jobId: [{}], taskId: [{}] is not alive, change the task status to [{}]",
                            jobTask.getJobId(), jobTask.getId(), ExecuteStatus.FAILD.name());
                    JobTask realTimeTask = taskService.get(jobTask.getId());
                    boolean needCheck = null != realTimeTask &&
                            (realTimeTask.getStatus().equals(ExecuteStatus.RUNNING.name()) ||
                                    realTimeTask.getStatus().equals(ExecuteStatus.RUNNING_TIMEOUT.name()));
                    if(!needCheck){
                        return;
                    }
                    jobTask.setStatus(ExecuteStatus.FAILD.name());
                    if(taskService.update(jobTask)) {
                        jobInfoService.sendInfoToAlarm(jobTask.getId(),
                                jobTask.getJobId(), AlarmTemplate.TITLE_TASK_RUN_FAID,
                                PatternInjectUtils.inject(AlarmTemplate.TASK_RUN_FAID,
                                        new Object[]{jobTask.getJobName(), jobTask.getJobId(), jobTask.getId()}, false, false,true));
                    }
                }
            });
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }
}
