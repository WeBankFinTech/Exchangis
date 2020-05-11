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

package com.webank.wedatasphere.exchangis.executor.backend;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for backend processes
 * @author davidhua
 * 2020/3/28
 */
@Component
public class BackendScheduler {

    private static Logger LOG = LoggerFactory.getLogger(BackendScheduler.class);

    private Scheduler scheduler;
    @PostConstruct
    public void init() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            this.scheduler.start();
            initScheduleJobs();
        }catch(Exception e){
            LOG.error("SCHEDULE_ERROR: [" + e.getMessage() +"]", e);
            System.exit(1);
        }
    }

    public void scheduleInterval(String id, Class<? extends Job> jobClass, long interval, TimeUnit timeUnit) {
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(id).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(id).withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(Math.toIntExact(timeUnit.toSeconds(interval)))
                                .repeatForever()
                ).build();
        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOG.error("SCHEDULE_ERROR: Fail to schedule interval: [" + e.getMessage() +"]", e);
        }
    }

    public void scheduleCorn(String id, Class<? extends Job> jobClass, String cornExpression){
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(id).build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cornExpression)
            .withMisfireHandlingInstructionDoNothing();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(id).withSchedule(cronScheduleBuilder).build();
        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOG.error("SCHEDULE_ERROR: Fail to schedule corn: [" + e.getMessage() +"]", e);
        }
    }

    public void stopSchedule(String id){
        try {
            this.scheduler.pauseJob(JobKey.jobKey(id));
        } catch (SchedulerException e) {
            LOG.error("SCHEDULE_ERROR: Fail to stop schedule job[" + id + "]: [" + e.getMessage() +"]", e);
        }
    }

    public void shutdown(){
        try {
            this.scheduler.shutdown();
        } catch (SchedulerException e) {
            //Ignore
        }
    }
    public void initScheduleJobs(){
        this.scheduleInterval("BACKEND-LOG-CLEAN", LogCleanerJob.class,1, TimeUnit.HOURS);
    }
}
