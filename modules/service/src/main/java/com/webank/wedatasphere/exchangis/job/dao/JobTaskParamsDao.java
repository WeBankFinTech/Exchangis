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
import com.webank.wedatasphere.exchangis.job.domain.JobTaskParams;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/5/21
 */
public interface JobTaskParamsDao {
    /**
     * Insert task parameter
     * @param taskParam
     */
    void insert(JobTaskParams taskParam);

    /**
     * Insert batch
     * @param params
     */
    void insertBatch(List<JobTaskParams> params);

    /**
     * Get task's parameters(Map)
     * @param taskId
     * @return
     */
    @MapRule(key="param_name", value="param_val")
    Map<String, Object> getMapByTask(long taskId);
    /**
     *
     * List task's parameter list
     * @param taskId
     * @return
     */
    List<JobTaskParams> listByTask(long taskId);

    /**
     * Delete
     * @param ids
     */
    void deleteByTaskIds(List<Object> ids);
}
