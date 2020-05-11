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

package com.webank.wedatasphere.exchangis.executor.listener;


import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;

/**
 * When executor run out of time, invoke the method
 * @author davidhua
 * 2019/2/20
 */
@FunctionalInterface
public interface TaskHandleListener {
    /**
     * handle timeout
     * @param taskProcess
     * @param message
     */
    void handle(TaskProcess taskProcess, String message);
}
