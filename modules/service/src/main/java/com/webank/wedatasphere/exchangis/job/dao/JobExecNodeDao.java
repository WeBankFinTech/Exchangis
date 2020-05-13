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

import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.job.domain.JobExecNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author enjoyyin
 * 2019/4/8
 */
public interface JobExecNodeDao {
    /**
     * Get available nodes by job id
     * @param id
     * @param heartbeat
     * @return
     */
    List<ExecutorNode> getAvailsByJobId(@Param("jobId") Long id, @Param("heartbeat")long heartbeat);

    /**
     * Get nodes by job id
     * @param id
     * @return
     */
    List<ExecutorNode> getNodesByJobId(Long id);
    /**
     * Insert
     * @param batch
     */
    void insertBatch(List<JobExecNode> batch);

    /**
     * Delete
     * @param batch
     */
    void deleteBatch(List<JobExecNode> batch);

    /**
     * Delete by job ids
     * @param jobIds
     */
    void deleteBatchByJobIds(List<Long> jobIds);

    /**
     * If exists the relational nodes of job
     * @param jobId
     * @return
     */
    Integer existsRelationNodes(Long jobId);
}
