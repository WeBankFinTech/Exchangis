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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by devendeng on 2018/8/31.
 */
public class TaskProduceBean extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(TaskProduceBean.class);

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        // load jobId
        JobKey jobKey = context.getTrigger().getJobKey();
        Long jobId = Long.valueOf(jobKey.getName());
        // trigger
        TaskUtil.produceTask(jobId);
    }

}