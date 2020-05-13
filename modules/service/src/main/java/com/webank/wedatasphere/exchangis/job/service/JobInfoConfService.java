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

import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;

/**
 * Wrap jobInfoService, add methods associated with job configuration
 * @author enjoyyin
 * 2018/10/30
 */
public interface JobInfoConfService {
    /**
     * Add
     * @param jobInfo
     * @return
     */
    boolean add(JobInfo jobInfo);

    /**
     * Get
     * @param id
     * @return
     */
    JobInfo get(Integer id);

    /**
     * Update
     * @param jobInfo
     * @return
     */
    boolean update(JobInfo jobInfo);

    /**
     * Run job
     * @param jobInfo
     * @return jobId
     */
    Long runJob(JobInfo jobInfo);

    /**
     * Generate template job info
     * @param src
     * @param dest
     * @return
     */
    JobInfo templateInfo(TypeEnums src, TypeEnums dest);
}
