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

package com.webank.wedatasphere.exchangis.executor.domain;

import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.domain.JobEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * @author devendeng on 2018/9/4.
 */
public class ExecuteReq {

    private Long jobId;

    private Long taskId;

    private String jobConfig;

    private JobEngine engine;

    private String execUser;

    private long timeout;

    private long startTime;

    @Deprecated
    private String procSrcCode;

    @Deprecated
    private Map<String, Object> params = new HashMap<>();

    private Map<String, Object> engineParams = new HashMap<>();

    private Map<String, Object> taskParams = new HashMap<>();

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(String jobConfig) {
        this.jobConfig = jobConfig;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, Object> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Map<String, Object> taskParams) {
        this.taskParams = taskParams;
    }


    public String getProcSrcCode() {
        return engineParams.get(DefaultParams.Engine.PARAM_PROC_SRC_CODE) == null?
                null : String.valueOf(engineParams.get(DefaultParams.Engine.PARAM_PROC_SRC_CODE));
    }


    public JobEngine getEngine() {
        return engine;
    }

    public void setEngine(JobEngine engine) {
        this.engine = engine;
    }


    public Map<String, Object> getEngineParams() {
        return engineParams;
    }

    public void setEngineParams(Map<String, Object> engineParams) {
        this.engineParams = engineParams;
    }

    @Override
    public String toString() {
        return "ExecuteReq{" +
                "jobId=" + jobId +
                ", taskId=" + taskId +
                ", jobConfig='" + jobConfig + '\'' +
                ", startTime=" + startTime +
                '}';
    }

}
