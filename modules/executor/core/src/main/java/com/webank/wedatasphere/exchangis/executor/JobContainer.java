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

package com.webank.wedatasphere.exchangis.executor;

import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author davidhua
 * 2019/11/12
 */
public class JobContainer {

    public static final Integer TASK_CODE_SUCCESS = 0;
    public static final Integer TASK_CODE_KILL = 143;
    public static final String LOG_SYS_NAME = "out.log";
    public static final String LOG_STDOUT_NAME = "stdout.log";
    public static final String LOG_STDERR_NAME = "stderr.log";
    public static final String PID_NAME = "pid";

    private JobExecutor jobExecutor;
    private TaskState taskState;
    private Runtime runtime;

    public class Runtime{
        private AtomicLong maxByteSpeed = new AtomicLong(0);

        public void limit(Long maxByteSpeed){
            //Just update the value simply
            this.maxByteSpeed.set(maxByteSpeed);
        }

        public Long getMaxByteSpeed(){
            return this.maxByteSpeed.get();
        }

        public void toStop(){
            if(jobExecutor.isAlive()) {
                jobExecutor.toStop();
            }
        }

        public LogResult log(long startLine, long windSize){
            return jobExecutor.log(startLine, windSize);
        }
    }
    public JobContainer(JobExecutor jobExecutor){
        this.jobExecutor = jobExecutor;
        this.taskState = jobExecutor.getTaskState();
        this.runtime = new Runtime();
    }

    /**
     * Update the task state information
     * @param state
     */
    public void updateTaskState(TaskState state){
        if(state.getCurrentByteSpeed() != null) {
            taskState.setCurrentByteSpeed(state.getCurrentByteSpeed());
        }
        taskState.setVersionTime(Calendar.getInstance().getTime());
    }


    public TaskState getState(){
        return new TaskState(this.taskState);
    }

    public Runtime getRuntime() {
         return this.runtime;
    }

    public long getJobId(){
        return this.jobExecutor.getJobId();
    }
}
