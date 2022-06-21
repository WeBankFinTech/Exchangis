package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.GenericExchangisJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Job could be executed
 */
public class LaunchableExchangisJob extends GenericExchangisJob {

    /**
     * Job info
     */
    private ExchangisJobInfo exchangisJobInfo;

    /**
     * Execution id
     */
    private String jobExecutionId;

    /**
     * Execute user
     */
    private String execUser;

    private List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>();

    public ExchangisJobInfo getExchangisJobInfo() {
        return exchangisJobInfo;
    }

    public void setExchangisJobInfo(ExchangisJobInfo exchangisJobInfo) {
        this.exchangisJobInfo = exchangisJobInfo;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public List<LaunchableExchangisTask> getLaunchableExchangisTasks() {
        return launchableExchangisTasks;
    }

    public void setLaunchableExchangisTasks(List<LaunchableExchangisTask> launchableExchangisTasks) {
        this.launchableExchangisTasks = launchableExchangisTasks;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }
}
