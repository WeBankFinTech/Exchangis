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

package com.webank.wedatasphere.exchangis.job.service;


import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;

public interface JobInfoService extends IBaseService<JobInfo> {
    /**
     * Resume job
     *
     * @param id
     * @return
     */
    boolean resume(Long id);

    /**
     * Pause
     *
     * @param id
     * @return
     */
    boolean pause(Long id);

    /**
     * Trigger job
     *
     * @param id
     * @return
     */
    boolean triggerJob(Long id);


    /**
     * If there are some jobs which run with the dataSource provided
     *
     * @param id
     * @return
     */
    boolean isRunWithDataSource(Long id);

    /**
     * Send information to alarm
     * @param taskId
     * @param jobInfo
     * @param alarmTitle
     * @param alarmInfo
     * @param defaultAlarmUser
     */
    void sendInfoToAlarm(long taskId, JobInfo jobInfo, String alarmTitle, String alarmInfo, boolean defaultAlarmUser);
    /**
     * Send information to alarm
     * @param taskId
     * @param jobInfo
     * @param alarmTitle
     * @param alarmInfo
     */
    void sendInfoToAlarm(long taskId, JobInfo jobInfo, String alarmTitle, String alarmInfo);

    /**
     * Send information to alarm
     * @param taskId
     * @param jobId
     * @param alarmTitle
     * @param alarmInfo
     */
    void sendInfoToAlarm(long taskId, long jobId, String alarmTitle, String alarmInfo);

}