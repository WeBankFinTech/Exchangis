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

package com.webank.wedatasphere.exchangis.scheduler.cluster;

import com.webank.wedatasphere.exchangis.job.JobConfiguration;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.scheduler.cluster.bean.TaskAliveCheckBean;
import com.webank.wedatasphere.exchangis.scheduler.cluster.bean.TaskProduceBean;
import com.webank.wedatasphere.exchangis.scheduler.cluster.bean.TaskQueueRepairBean;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by devendeng on 2018/8/30.
 */
@Service
public class JobDynamicScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobDynamicScheduler.class);
    @Resource
    private Scheduler scheduler;

    @Resource
    private JobConfiguration.TaskAlive taskConf;

    @Resource
    private JobConfiguration.TaskQueueRepair taskQueueRepair;

    @PostConstruct
    public void startSchedulers(){
        startTaskAliveChecker();
        startTaskQueueRepair();
    }

    private void startTaskAliveChecker(){
        String idKey = taskConf.getAliveSchedId();
        int interval = taskConf.getAliveSchedInterval();
        TriggerKey triggerKey = TriggerKey.triggerKey(idKey);
        try {
            if(!scheduler.checkExists(triggerKey)){
                logger.info("Start a task alive checker, jobKey: [{}], triggerKey: [{}], interval in seconds: [{}]",
                        idKey, idKey, interval);
                JobKey jobKey = new JobKey(idKey);
                SimpleScheduleBuilder builder =SimpleScheduleBuilder.simpleSchedule();
                builder.withIntervalInSeconds(interval).repeatForever();
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .withSchedule(builder).build();
                Class<? extends Job> jobClass = TaskAliveCheckBean.class;
                JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey)
                        .usingJobData(SchedulerClusterConstants.ALIVE_CHECK_NUM, taskConf.getAliveSchedCheckNum())
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Start task alive checker succeed, jobKey: [{}], triggerKey: [{}]", idKey, idKey);
            }else{
                Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
                if(state == Trigger.TriggerState.ERROR){
                    logger.error("SCHEDULER_ERROR,  jobKey:[{}], triggerKey: [{}] status: ERROR", idKey, idKey);
                    scheduler.resetTriggerFromErrorState(triggerKey);
                    logger.info("Reset trigger task [{}] alive checker", triggerKey);
                }
            }
        } catch (SchedulerException e) {
            logger.error("SCHEDULER_ERROR, jobKey:[{}], triggerKey: [{}], message: [{}]", idKey, idKey, e.getMessage(), e);
        }
    }

    private void startTaskQueueRepair(){
        String idKey = taskQueueRepair.getQueueRepairSchedId();
        int interval= taskQueueRepair.getQueueRepairInterval();
        TriggerKey triggerKey = TriggerKey.triggerKey(idKey);
        try{
            if(!scheduler.checkExists(triggerKey)){
                logger.info("Start a queue repair, jobKey:[{}], triggerKey:[{}], interval in seconds: [{}]",
                        idKey, idKey, interval);
                JobKey jobKey = new JobKey(idKey);
                SimpleScheduleBuilder builder =SimpleScheduleBuilder.simpleSchedule();
                builder.withIntervalInSeconds(interval).repeatForever();
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .withSchedule(builder).build();
                Class<? extends Job> jobClass = TaskQueueRepairBean.class;
                JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey)
                        .usingJobData(SchedulerClusterConstants.REPAIR_POLL_THRESHOLD, taskQueueRepair.getQueueRepairThreshold())
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
                logger.info("Start a queue repair succeed, jobKey: [{}], triggerKey: [{}]", idKey, idKey);
            }else{
                Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
                if(state == Trigger.TriggerState.ERROR){
                    logger.error("SCHEDULER_ERROR,  jobKey:[{}], triggerKey: [{}] status: ERROR", idKey, idKey);
                    scheduler.resetTriggerFromErrorState(triggerKey);
                    logger.info("Reset trigger task [{}] alive checker", triggerKey);
                }
            }
        }catch (SchedulerException e) {
            logger.error("SCHEDULER_ERROR, jobKey:[{}], triggerKey: [{}], message: [{}]", idKey, idKey, e.getMessage(), e);
        }
    }
    /**
     * fill job info
     *
     * @param jobInfo
     */
    public void fillJobInfo(JobInfo jobInfo) {
        // TriggerKey : name
        String name = String.valueOf(jobInfo.getId());
        TriggerKey triggerKey = TriggerKey.triggerKey(name);

        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);

            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);

            // parse params
            if (trigger != null && trigger instanceof CronTriggerImpl) {
                String cronExpression = ((CronTriggerImpl) trigger).getCronExpression();
                jobInfo.setJobCorn(cronExpression);
            }

            if (triggerState != null) {
                jobInfo.setJobStatus(triggerState.name());
            }

        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * check if exists
     *
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean checkExists(String jobName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
        return scheduler.checkExists(triggerKey);
    }

    /**
     * addJob
     *
     * @param jobName
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
    public boolean addJob(String jobName, String cronExpression) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
        JobKey jobKey = new JobKey(jobName);

        // TriggerKey valid if_exists
        if (checkExists(jobName)) {
            logger.info("AddJob fail, job already exist, jobName:[{}]", jobName);
            return false;
        }

        // CronTrigger : TriggerKey + cronExpression	// withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // JobDetail : jobClass
        Class<? extends Job> jobClass = TaskProduceBean.class;

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        // schedule : jobDetail + cronTrigger
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        logger.info("AddJob success, jobDetail:[{}], cronTrigger:[{}], date:[{}]", jobDetail, cronTrigger, date);
        return true;
    }

    /**
     * rescheduleJob
     *
     * @param jobName
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
    public boolean rescheduleJob(String jobName, String cronExpression) throws SchedulerException {

        // TriggerKey valid if_exists
        if (!checkExists(jobName)) {
            logger.info("RescheduleJob fail, job not exists, JobName:[{}]", jobName);
            return false;
        }

        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
        CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        if (oldTrigger != null) {
            // avoid repeat
            String oldCron = oldTrigger.getCronExpression();
            if (oldCron.equals(cronExpression)) {
                return true;
            }

            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            oldTrigger = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // rescheduleJob
            scheduler.rescheduleJob(triggerKey, oldTrigger);
        } else {
            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // JobDetail-JobDataMap fresh
            JobKey jobKey = new JobKey(jobName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            // Trigger fresh
            HashSet<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(cronTrigger);

            scheduler.scheduleJob(jobDetail, triggerSet, true);
        }

        logger.info("ResumeJob success, JobName:[{}]", jobName);
        return true;
    }

    /**
     * unscheduleJob
     *
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean removeJob(String jobName) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
        boolean result = false;
        if (checkExists(jobName)) {
            result = scheduler.unscheduleJob(triggerKey);
            logger.info("RemoveJob, triggerKey:[{}], result [{}]", triggerKey, result);
        }
        return true;
    }

    /**
     * pause
     *
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean pauseJob(String jobName) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);

        boolean result = false;
        if (checkExists(jobName)) {
            scheduler.pauseTrigger(triggerKey);
            result = true;
            logger.info("pauseJob success, triggerKey:[{}]", triggerKey);
        } else {
            logger.info("pauseJob fail, triggerKey:[{}]", triggerKey);
        }
        return result;
    }

    /**
     * resume
     *
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean resumeJob(String jobName) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName);

        boolean result = false;
        if (checkExists(jobName)) {
            Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
            if(state == Trigger.TriggerState.ERROR){
                scheduler.resetTriggerFromErrorState(triggerKey);
            }
            scheduler.resumeTrigger(triggerKey);
            result = true;
            logger.info("ResumeJob success, triggerKey:[{}]", triggerKey);
        } else {
            logger.info("ResumeJob fail, triggerKey:[{}]", triggerKey);
        }
        return result;
    }

    /**
     * run
     *
     * @param jobName
     * @return
     * @throws SchedulerException
     */
    public boolean triggerJob(String jobName) throws SchedulerException {
        // TriggerKey : name + group
        JobKey jobKey = new JobKey(jobName);

        boolean result = false;
        if (checkExists(jobName)) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            scheduler.triggerJob(jobKey);
            result = true;
            logger.info("RunJob success, jobKey:[{}]", jobKey);
        } else {
            logger.info("RunJob fail, jobKey:[{}]", jobKey);
        }
        return result;
    }

}
