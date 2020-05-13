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

package com.webank.wedatasphere.exchangis.job.dao;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.job.domain.JobTask;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author devendeng on 2018/8/24.
 */
public interface JobTaskDao extends IBaseDao<JobTask> {
     /**
      * Get task list by Id list
      * @param id
      * @return
      */
     List<JobTask> selectByIdList(List<Long> id);

     /**
      *
      * Find running(and timeout) task's batches
      * @param idBegin the taskId began to find
      * @param batchSize the batch size
      * @return
      */
     List<JobTask> findRunInBatch(@Param("taskId")long idBegin, @Param("batchSize")int batchSize);

     /**
      * Find task's batches by jobId and time range
      * @param jobId
      * @param startTime
      * @param endTime
      * @param idBegin
      * @param batchSize
      * @return
      */
     List<JobTask> findByJobAndTimeInBatch(@Param("jobId") int jobId,
                                           @Param("startTime")Date startTime, @Param("endTime")Date endTime,
                                           @Param("taskId")long idBegin, @Param("batchSize") int batchSize);

     /**
      * Only update the state columns
      * @param jobTasks
      */
     void updateStates(List<JobTask> jobTasks);

     /**
      * Limit speed
      * @param taskId
      * @param speedLimit
      */
     void limitSpeed(@Param("id")long taskId, @Param("speedLimit")int speedLimit);

     /**
      * Select for update
      * @param taskId
      * @return
      */
     JobTask selectOneAndLock(long taskId);
}
