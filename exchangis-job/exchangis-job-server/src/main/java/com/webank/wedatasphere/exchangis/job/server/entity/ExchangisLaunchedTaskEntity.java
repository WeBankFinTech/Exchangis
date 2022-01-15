package com.webank.wedatasphere.exchangis.job.server.entity;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskMetricsDTO;

import java.util.Date;

/**
 * @author tikazhang
 */
public class ExchangisLaunchedTaskEntity {
    private Long id;

    private String name;

    private Date createTime;

    private Date lastUpdateTime;

    private String engineType;

    private String executeUser;

    private Long jobId;

    private String jobName;

    private Long enchangisLaunchableTaskId;

    private Long progress;

    private String errorCode;

    private String errorMsg;

    private String retryNum;

    private Long taskId;

    private String linkisJobId;

    private String linkisJobInfo;

    private String jobExecutionId;

    private Date launchTime;

    private Date runningTime;

    private String metric;

    private ExchangisTaskMetricsDTO metrics;

    public ExchangisTaskMetricsDTO getMetricsDTO() {
        return metrics;
    }

    public ExchangisLaunchedTaskEntity(){}

    public ExchangisLaunchedTaskEntity(Long taskId, String name, String status, Date createTime, Date launchTime, Date lastUpdateTime, String engineType, String linkisJobId, String linkisJobInfo, String executeUser){
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.createTime = createTime;
        this.launchTime = launchTime;
        this.lastUpdateTime = lastUpdateTime;
        this.engineType = engineType;
        this.linkisJobId = linkisJobId;
        this.linkisJobInfo = linkisJobInfo;
        this.executeUser = executeUser;
    }

    public ExchangisLaunchedTaskEntity(Long taskId, String name, String status, ExchangisTaskMetricsDTO metrics){
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.metrics = metrics;
    }

    public void setMetricsDTO(ExchangisTaskMetricsDTO taskMetrics) {
        this.metrics = taskMetrics;
    }

    private String status;

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

    public Long getEnchangisLaunchableTaskId() {
        return enchangisLaunchableTaskId;
    }

    public void setEnchangisLaunchableTaskId(Long enchangisLaunchableTaskId) {
        this.enchangisLaunchableTaskId = enchangisLaunchableTaskId;
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

    public String getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(String retryNum) {
        this.retryNum = retryNum;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getLinkisJobId() {
        return linkisJobId;
    }

    public void setLinkisJobId(String linkisJobId) {
        this.linkisJobId = linkisJobId;
    }

    public String getLinkisJobInfo() {
        return linkisJobInfo;
    }

    public void setLinkisJobInfo(String linkisJobInfo) {
        this.linkisJobInfo = linkisJobInfo;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }

    public Date getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(Date runningTime) {
        this.runningTime = runningTime;
    }

    public String getMetrics() {
        return metric;
    }

    public void setMetrics(String metrics) {
        this.metric = metrics;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
