package com.webank.wedatasphere.exchangis.job.launcher.entity;

import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

import java.util.Date;

/**
 * Inheritable task entity
 */
public class GenericExchangisTaskEntity implements ExchangisTaskEntity{

    protected Long id;

    protected String name;

    protected Date createTime;

    protected Date lastUpdateTime;

    protected String engineType;

    protected String executeUser;

    protected Long jobId;

    protected String jobName;

    protected TaskStatus status = TaskStatus.Inited;

    protected double progress = 0.0;

    protected Integer errorCode;

    protected String errorMessage;

    protected Integer retryNum = 0;

    protected String createUser;

    @Override
    public Long getJobId() {
        return this.jobId;
    }

    @Override
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Override
    public String jobName() {
        return this.jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public TaskStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public double getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public Integer getErrorCode() {
        return this.errorCode;
    }

    @Override
    public void setErrorCode(Integer code) {
        this.errorCode = code;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Integer getRetryNum() {
        return this.retryNum;
    }

    @Override
    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    @Override
    public String getEngineType() {
        return engineType;
    }

    @Override
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    @Override
    public String getExecuteUser() {
        return this.executeUser;
    }

    @Override
    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
