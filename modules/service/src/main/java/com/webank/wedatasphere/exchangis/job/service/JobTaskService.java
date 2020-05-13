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
import com.webank.wedatasphere.exchangis.job.domain.*;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/2/13
 */

public interface JobTaskService extends IBaseService<JobTask> {
    /**
     * task status list
     * @param idList
     * @return
     */
    List<String> statusList(List<Long> idList);

    /**
     * check if (all) the job tasks running is alive in batch
     * @param batchSize batch size
     * @param callback callback
     */
    void checkAliveInBatch(int batchSize, JobTaskAliveCallback callback);

    /**
     * check if the job task associated with 'taskId' is alive
     * @param taskId task id
     */
    boolean checkAlive(int taskId);

    /**
     * run task by element in queue
     * @param taskQueueElement
     * @return
     */
    boolean runTask(QueueElement taskQueueElement);
    /**
     * insert task and then add to queue selected
     * @param task
     */
    void insertTaskAndAddToQueue(JobTask task);

    /**
     * insert task and the task parameters, then add to queue selected
     * @param task
     * @param params
     */
    void insertTaskAndAddToQueue(JobTask task, List<JobTaskParams> params);

    /**
     * update task and then add to queue selected
     * @param task
     */
    void updateTaskAndAddToQueue(JobTask task);
    /**
     * remove element related with task in queue
     * @param taskId
     */
    void removeFromQueue(long taskId);

    /**
     * add task parameters
     * @param params
     */
    void addTaskParams(List<JobTaskParams> params);

    /**
     * run history data
     * @param jobInfo
     * @param req
     * @param userName
     */
    void runHistoryTaskByTime(JobInfo jobInfo, JobHistoryReq req, String userName);

    /**
     * get task's parameters by task id
     * @param taskId
     * @return
     */
    Map<String, Object> getTaskParameters(long taskId);

    /**
     * update state of task
     * @param address
     * @param stateList
     */
    void updateTaskState(String address, List<TaskState> stateList);

    /**
     * limit speed
     * @param jobTask
     * @param speedLimit
     */
    void limitSpeed(JobTask jobTask, int speedLimit);

    /**
     * Select and update
     * @param jobTask
     * @return
     */
    boolean selectAndUpdate(JobTask jobTask);
}
