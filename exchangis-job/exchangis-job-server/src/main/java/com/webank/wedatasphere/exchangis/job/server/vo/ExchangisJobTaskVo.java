package com.webank.wedatasphere.exchangis.job.server.vo;

import java.util.Date;

/**
 *
 * @Date 2022/1/12 22:34
 */

public class ExchangisJobTaskVo {

    private String name;

    private Date createTime;

    private String status;

    private Date lastUpdateTime;

    private String engineType;

    private String executeUser;

    private String taskId;

    private String linkisJobId;

    private String linkisJobInfo;

    private Date launchTime;

    public ExchangisJobTaskVo(){

    }

    public ExchangisJobTaskVo(String taskId, String name, String status, Date createTime, Date launchTime, Date lastUpdateTime, String engineType, String linkisJobId, String linkisJobInfo, String executeUser){
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
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

    public Date getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }
}
