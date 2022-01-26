package com.webank.wedatasphere.exchangis.job.launcher.entity;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;

import java.util.Calendar;

/**
 * Entity to persist the launched job
 */
public class LaunchedExchangisJobEntity extends GenericExchangisTaskEntity{

    /**
     * Execution id
     */
    private String jobExecutionId;

    /**
     * Log path, could be a uri
     */
    private String logPath;

    /**
     * Number of launchable task
     */
    private int launchableTaskNum = 0;

    public LaunchedExchangisJobEntity(){

    }
    public LaunchedExchangisJobEntity(LaunchableExchangisJob job){
        this.name = job.getName();
        this.engineType = job.getEngineType();
        this.jobId = job.getId();
        this.jobName = job.getName();
        // Use the create user of LaunchableExchangisJob as exec user
        this.executeUser = job.getCreateUser();
        this.createTime = job.getCreateTime();
        this.lastUpdateTime = job.getLastUpdateTime();
        this.jobExecutionId = job.getJobExecutionId();
        this.logPath = this.executeUser + "/" + this.jobExecutionId;
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

    public int getLaunchableTaskNum() {
        return launchableTaskNum;
    }

    public void setLaunchableTaskNum(int launchableTaskNum) {
        this.launchableTaskNum = launchableTaskNum;
    }
}
