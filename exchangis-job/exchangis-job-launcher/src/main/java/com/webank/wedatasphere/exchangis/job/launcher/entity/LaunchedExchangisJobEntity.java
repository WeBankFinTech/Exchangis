package com.webank.wedatasphere.exchangis.job.launcher.entity;

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
    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }
}
