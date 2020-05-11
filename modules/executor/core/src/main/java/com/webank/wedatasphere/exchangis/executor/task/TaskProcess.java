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

package com.webank.wedatasphere.exchangis.executor.task;

import com.webank.wedatasphere.exchangis.executor.exception.TaskResAllocException;
import com.webank.wedatasphere.exchangis.executor.resource.Resource;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;

import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * @author devendeng
 * @date 2018/9/6
 */
public interface TaskProcess {
    /**
     * Init task
     */
     void init();

    /**
     * Run task (blocking)
     * @throws TaskResAllocException if cannot allocate resource
     * @return run result
     */
     int execute() throws TaskResAllocException;

    /**
     * Run task (Non-blocking)
     * @throws TaskResAllocException if cannot allocate resource
     * @return future
     */
    Future<Integer> executeAsync() throws TaskResAllocException;

    /**
     * Get the task log in window size
     * @param startLine start line
     * @param windSize wind size
     * @return log
     */
    LogResult log(long startLine, long windSize);


    /**
     * Get input stream if it has
     * @return input stream
     */
    InputStream getInputStream();

    /**
     * Get error stream if it has
     * @return error stream
     */
    InputStream getErrorStream();

    /**
     * Finish task
     */
    void destroy();

    /**
     * Check if the process is alive
     * @return if alive
     */
    boolean isAlive();


    /**
     * get resource requirement
     * @return resource
     */
    Resource getResource();
    /**
     * release resource
     */
    void clean();

}
