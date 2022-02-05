package com.webank.wedatasphere.exchangis.job.launcher.domain;

import java.util.Date;

/**
 * @author tikazhang
 * @Date 2022/1/25 14:36
 */
public class LaunchedJobHistory {

    private String jobExecutionId;

    private String executeNode;

    private String name;

    private Date createTime;

    private Long flow;

    private String executeUser;

    private String status;

    private double progress;

    private Date lastUpdateTime;

    public LaunchedJobHistory(String jobExecutionId, String executeNode, String name, Date createTime, Long flow, String executeUser, String status, double progress, Date lastUpdateTime){
        this.jobExecutionId = jobExecutionId;
        this.executeNode = executeNode;
        this.name = name;
        this.status = status;
        this.createTime = createTime;
        this.flow = flow;
        this.lastUpdateTime = lastUpdateTime;
        this.progress = progress;
        this.lastUpdateTime = lastUpdateTime;
        this.executeUser = executeUser;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getExecuteNode() {
        return executeNode;
    }

    public void setExecuteNode(String executeNode) {
        this.executeNode = executeNode;
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

    public Long getFlow() {
        return flow;
    }

    public void setFlow(Long flow) {
        this.flow = flow;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
