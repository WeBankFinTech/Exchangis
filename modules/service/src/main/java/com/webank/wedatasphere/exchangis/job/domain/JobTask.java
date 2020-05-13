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

package com.webank.wedatasphere.exchangis.job.domain;



import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Job task info
 * @author devendeng on 2018/8/24.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobTask {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotBlank(message = "{udes.domain.jobTask.address.notBlank}")
    private String executerAddress;
    @NotBlank(message = "{udes.domain.jobInfo.id.notNull}")
    private Long jobId;
    /**
     * Trigger type: scheduler or api
     */
    private String triggerType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date triggerTime;
    private String triggerStatus;
    private String triggerMsg;
    /**
     * Operator
     */
    private String operater;
    private String status;
    /**
     * Run times
     */
    private Integer runTimes;
    private String executeMsg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;
    private String jobName;
    private String jobCreateUser;
    private String jobAlarmUser;
    private String execUser;
    /**
     * Version date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date version;

    private boolean disposable = false;

    @JsonIgnore
    private Long stateSpeed;

    /**
     * speed limit
     */
    private Integer speedLimit;

    /**
     * Project id
     */
    private Long projectId;

    private Map<String, Object> taskParams = new HashMap<>();

    public JobTask(){

    }

    public JobTask(JobInfo jobInfo, String triggerType, String operater){
        Calendar calendar = Calendar.getInstance();
        this.jobId = jobInfo.getId();
        this.jobName = jobInfo.getJobName();
        this.jobCreateUser = jobInfo.getCreateUser();
        this.jobAlarmUser = jobInfo.getAlarmUser();
        this.projectId = jobInfo.getProjectId();
        this.triggerTime = calendar.getTime();
        this.triggerStatus = ExecuteStatus.SUCCESS.name();
        this.status = ExecuteStatus.COMMIT.name();
        this.operater = operater;
        this.triggerType = triggerType;
        this.runTimes = 0;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExecuterAddress() {
        return executerAddress;
    }

    public void setExecuterAddress(String executerAddress) {
        this.executerAddress = executerAddress;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(String triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public String getTriggerMsg() {
        return triggerMsg;
    }

    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(Integer runTimes) {
        this.runTimes = runTimes;
    }

    public String getExecuteMsg() {
        return executeMsg;
    }

    public void setExecuteMsg(String executeMsg) {
        this.executeMsg = executeMsg;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public boolean isDisposable() {
        return disposable;
    }

    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobCreateUser() {
        return jobCreateUser;
    }

    public void setJobCreateUser(String jobCreateUser) {
        this.jobCreateUser = jobCreateUser;
    }

    public Map<String, Object> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Map<String, Object> taskParams) {
        this.taskParams = taskParams;
    }

    public String getJobAlarmUser() {
        return jobAlarmUser;
    }

    public void setJobAlarmUser(String jobAlarmUser) {
        this.jobAlarmUser = jobAlarmUser;
    }

    public Long getStateSpeed() {
        return stateSpeed;
    }

    public void setStateSpeed(Long stateSpeed) {
        this.stateSpeed = stateSpeed;
    }

    public String getSpeed(){
        boolean viewSpeed = (status.equalsIgnoreCase(ExecuteStatus.RUNNING.name()) || status.equalsIgnoreCase(ExecuteStatus.RUNNING_TIMEOUT.name()))
                && this.stateSpeed != null;
        if(viewSpeed){
            BigDecimal bd = BigDecimal.valueOf((double)this.stateSpeed / 1048576);
            return String.valueOf(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        return "---";
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
