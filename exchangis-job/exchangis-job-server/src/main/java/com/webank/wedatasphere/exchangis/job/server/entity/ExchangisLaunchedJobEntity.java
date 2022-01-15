package com.webank.wedatasphere.exchangis.job.server.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tikazhang
 */
public class ExchangisLaunchedJobEntity {
    private Long id;

    private String name;

    private Date createTime;

    private Date lastUpdateTime;

    private String engineType;

    private String executeUser;

    private Long jobId;

    private String jobName;

    private String status;

    private Long progress;

    private String errorCode;

    private String errorMsg;

    private Long retryNum;

    private String jobExecutionId;

    private String logPath;

    private Map<String, Object> tasks;

    private List<ExchangisLaunchedTaskEntity> launchedRunningTaskList;

    private List<ExchangisLaunchedTaskEntity> launchedInitedTaskList;

    private List<ExchangisLaunchedTaskEntity> launchedSuccessTaskList;

    public ExchangisLaunchedJobEntity(){

    }

    public ExchangisLaunchedJobEntity(String status, Long progress, Map<String, Object> tasks) {
        this.status = status;
        this.progress = progress;
        this.tasks = tasks;
    }

    public List<ExchangisLaunchedTaskEntity> getLaunchedRunningTaskList() {
        return launchedRunningTaskList;
    }

    public void setLaunchedRunningTaskList(List<ExchangisLaunchedTaskEntity> launchedRunningTaskList) {
        this.launchedRunningTaskList = launchedRunningTaskList;
    }

    public List<ExchangisLaunchedTaskEntity> getLaunchedInitedTaskList() {
        return launchedInitedTaskList;
    }

    public void setLaunchedInitedTaskList(List<ExchangisLaunchedTaskEntity> launchedInitedTaskList) {
        this.launchedInitedTaskList = launchedInitedTaskList;
    }

    public List<ExchangisLaunchedTaskEntity> getLaunchedSuccessTaskList() {
        return launchedSuccessTaskList;
    }

    public void setLaunchedSuccessTaskList(List<ExchangisLaunchedTaskEntity> launchedSuccessTaskList) {
        this.launchedSuccessTaskList = launchedSuccessTaskList;
    }

    public Map<String, Object> getLaunchedTaskEntityList() {
        return tasks;
    }

    public void setLaunchedTaskEntityList(Map<String, Object> tasks) {
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Long getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Long retryNum) {
        this.retryNum = retryNum;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
