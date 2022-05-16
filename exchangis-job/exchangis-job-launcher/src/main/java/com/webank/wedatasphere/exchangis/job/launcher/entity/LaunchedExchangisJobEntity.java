package com.webank.wedatasphere.exchangis.job.launcher.entity;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Entity to persist the launched job
 */
public class LaunchedExchangisJobEntity extends GenericExchangisTaskEntity{

    /**
     * Execution id
     */
    private String jobExecutionId;

    /**
     * ExchangisJobEntity
     */

    private ExchangisJobEntity exchangisJobEntity;

    /**
     * Log path, could be a uri
     */
    private String logPath;

    /**
     * Number of launchable task
     */
    private int launchableTaskNum = 0;

    public ExchangisJobEntity getExchangisJobEntity() {
        return exchangisJobEntity;
    }

    public void setExchangisJobEntity(ExchangisJobEntity exchangisJobEntity) {
        this.exchangisJobEntity = exchangisJobEntity;
    }


    public LaunchedExchangisJobEntity(){

    }
    public LaunchedExchangisJobEntity(LaunchableExchangisJob job){
        this.name = job.getName();
        this.engineType = job.getEngineType();
        this.jobId = job.getId();
        this.jobName = job.getName();
        this.executeUser = job.getExecUser();
        this.createUser = job.getCreateUser();
        this.createTime = job.getCreateTime();
        this.lastUpdateTime = job.getLastUpdateTime();
        this.jobExecutionId = job.getJobExecutionId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.logPath = this.executeUser + IOUtils.DIR_SEPARATOR_UNIX +
                simpleDateFormat.format(new Date()) + IOUtils.DIR_SEPARATOR_UNIX + this.jobExecutionId;
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
