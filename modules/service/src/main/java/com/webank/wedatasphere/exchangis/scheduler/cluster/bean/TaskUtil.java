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

import com.webank.wedatasphere.exchangis.SpringContext;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.controller.JobInfoController;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.job.domain.JobTask;
import com.webank.wedatasphere.exchangis.job.domain.JobTaskParams;
import com.webank.wedatasphere.exchangis.job.service.JobInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

import static com.webank.wedatasphere.exchangis.alarm.AlarmTemplate.JOB_SCHEDULER_ERROR;
import static com.webank.wedatasphere.exchangis.alarm.AlarmTemplate.TITLE_JOB_SCHEDULER_ERROR;

/**
 * Created by devendeng on 2018/8/31.
 * @author devendeng
 *
 */
public class TaskUtil {
    private static Logger logger = LoggerFactory.getLogger(TaskUtil.class);

    /**
     * Trigger job
     *
     * @param jobId
     */
    public static void produceTask(long jobId) {
        JobInfoController jobInfoController = SpringContext.getBean(JobInfoController.class);
        JobInfoService jobInfoService = SpringContext.getBean(JobInfoService.class);
        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        try {
            jobInfoController.scheduleRun(jobId);
        }catch(Exception e){
            JobInfo jobInfo = jobInfoService.get(jobId);
            String message = null;
            if(e instanceof EndPointException){
                EndPointException ee = (EndPointException)e;
                Locale locale = LocaleContextHolder.getLocale();
                message = ee.getUiMessage();
                try {
                    message = messageSource.getMessage(ee.getUiMessage(), null, locale);
                }catch(Exception ex){
                    //Ignore
                }
                if(ee.getArgs() != null && ee.getArgs().length > 0){
                    message = PatternInjectUtils.inject(message, ee.getArgs());
                }
            }else{
                message = e.getMessage();
            }
            jobInfoService.sendInfoToAlarm(-1, jobId, TITLE_JOB_SCHEDULER_ERROR,
                    PatternInjectUtils.inject(JOB_SCHEDULER_ERROR, new String[]{jobInfo.getJobName(), message},
                            false, false, true));
        }
    }

    public static List<JobTaskParams> generateIncrParams(JobInfo jobInfo, JobTask jobTask){
        //Add increment timestamp parameters
        List<JobTaskParams> paramsList = new ArrayList<>();
        paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_INCR_BEGIN_TIME,
                jobInfo.getLastTriggerTime() != null? String.valueOf(jobInfo.getLastTriggerTime().getTime()): "0"));
        paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_INCR_END_TIME,
                String.valueOf(jobTask.getTriggerTime().getTime())));
        return paramsList;
    }

}
