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
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;

/**
 * When the task is completed (fail or success), invoke the listener
 * Created by devendeng on 2018/9/6.
 * @author devendeng
 */
public interface TaskCompleteListener {

    /**
     * Handle
     * @param process
     * @param status
     * @param message
     */
     void handleComplete(TaskProcess process, ExecuteStatus status, String message);
}
