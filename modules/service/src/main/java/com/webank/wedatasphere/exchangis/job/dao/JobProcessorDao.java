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

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author enjoyyin
 * 2019/8/22
 */
public interface JobProcessorDao {

    /**
     * Insert processor
     * @param jobId
     * @param procSrcCode
     */
    void insert(@Param("jobId") Long jobId,
                @Param("procSrcCode")String procSrcCode);

    /**
     * Delete
     * @param jobId
     */
    void delete(Long jobId);

    /**
     * Delete batch
     * @param jobIds
     */
    void deleteBatch(List<Long> jobIds);

    /**
     * Fetch source code
     * @param jobId
     * @return
     */
    String fetchSrcCode(Long jobId);
}
