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

import com.webank.wedatasphere.exchangis.job.domain.JobTask;

/**
 * @author enjoyyin
 * 2019/2/28
 */
public interface JobTaskAliveCallback {
    /**
     * Callback method, alive
     * @param jobTask job task
     */
    void alive(JobTask jobTask);

    /**
     * Callback method, not alive
      * @param jobTask job task
     */
    void notAlive(JobTask jobTask);
}
