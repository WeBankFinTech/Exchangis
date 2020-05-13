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

import com.webank.wedatasphere.exchangis.common.mybatis.plugin.MapRule;
import com.webank.wedatasphere.exchangis.job.domain.JobInfoParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/12/26
 */
public interface JobInfoParamsDao {

    /**
     * Insert batch
     * @param paramsList parameter list
     */
    void insertBatch(List<JobInfoParams> paramsList);

    /**
     * Update batch
     * @param paramsList
     */
    void updateBatch(List<JobInfoParams> paramsList);

    /**
     * Delete batch
     * @param jobId job id
     * @param keySet key set
     */
    void deleteBatch(@Param("jobId") Long jobId, @Param("keys") List<String> keySet);


    /**
     * Delete batch by job id
     * @param jobIds
     */
    void deleteBatchByJobIds(List<Long> jobIds);

    /**
     * Get job's parameters(Map)
     * @param jobId job id
     * @return
     */
    @MapRule(key="param_name", value="param_val")
    Map<String, String> getParamsMapByJobId(long jobId);

}
