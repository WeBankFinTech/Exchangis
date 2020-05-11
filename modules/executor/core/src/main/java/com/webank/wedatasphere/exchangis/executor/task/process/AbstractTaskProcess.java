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

package com.webank.wedatasphere.exchangis.executor.task.process;

import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.task.TaskProcess;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author davidhua
 * 2019/12/22
 */
public abstract class AbstractTaskProcess implements TaskProcess {
    private final long jobId;

    private final long taskId;

    private final String execUser;

    private final TaskConfiguration taskConfig;

    private final ExecutorConfiguration execConfig;

    private final Map<String, Object> engineParams;

    protected File workDir;

    private final TaskState taskState;


    AbstractTaskProcess(long jobId, long taskId,
                        String execUser,
                        TaskConfiguration taskConfig, ExecutorConfiguration execConfig,
                        Map<String, Object> engineParams){
        this.jobId = jobId;
        this.taskId = taskId;
        this.execUser = execUser;
        this.taskConfig = taskConfig;
        this.execConfig = execConfig;
        if(null == engineParams){
            this.engineParams = new HashMap<>();
        }else{
            this.engineParams = engineParams;
        }
        this.taskState = new TaskState(taskId);
    }
    /**
     * Get jobId
     * @return value
     */
    protected final long getJobId(){
        return this.jobId;
    }
    /**
     * Get taskId
     * @return value
     */
    protected final long getTaskId(){
        return this.taskId;
    }

    protected final String getExecUser(){
        return execUser;
    }

    protected final TaskConfiguration getTaskConfig(){
        return taskConfig;
    }

    protected final Map<String, Object> getEngineParams(){
        return engineParams;
    }

    protected final TaskState getTaskState(){
        return taskState;
    }

    protected final ExecutorConfiguration getExecConfig(){
        return execConfig;
    }

    public final File getWorkDir() {
        return workDir;
    }

}
