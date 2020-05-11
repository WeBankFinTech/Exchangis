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

package com.webank.wedatasphere.exchangis.executor.service;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteReq;
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;

/**
 * Created by devendeng on 2018/9/6.
 * @author devendeng
 */
public interface ExecutorService {
    /**
     *  Run
     * @param req
     * @param execUser
     * @return
     */
    boolean run(ExecuteReq req, String execUser);

    /**
     *  Kill
     * @param jobId
     * @param taskId
     * @return
     */
    Response<String> kill(long jobId, long taskId);

    /**
     *  Get container
     * @param containerId
     * @return
     */
    JobContainer getJobContainer(Object containerId);

    /**
     *  log
     * @param jobId
     * @param taskId
     * @param startLine
     * @return
     */
    LogResult log(long jobId,long taskId,int startLine, int windSize);

    /**
     * is alive
     * @param jobId
     * @param taskId
     * @return
     */
    boolean isAlive(long jobId, long taskId);
}
