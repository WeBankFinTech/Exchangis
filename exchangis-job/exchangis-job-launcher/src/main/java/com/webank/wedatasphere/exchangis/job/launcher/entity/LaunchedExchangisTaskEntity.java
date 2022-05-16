package com.webank.wedatasphere.exchangis.job.launcher.entity;


import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Entity to persist the launched task
 */
public class LaunchedExchangisTaskEntity extends GenericExchangisTaskEntity {

    /**
     * Default, the task id has the same value with id
     */
    private String taskId;

    private String linkisJobId;

    private String linkisJobInfo;

    /**
     * Execution id
     */
    private String jobExecutionId;

    private Date launchTime;

    private Date runningTime;

    private String metrics;

    private LaunchableExchangisTask launchableExchangisTask;

    private Map<String, Object> linkisJobInfoMap;

    private Map<String, Object> metricsMap;

    public LaunchedExchangisTaskEntity(){

    }

    public LaunchedExchangisTaskEntity(LaunchableExchangisTask launchableExchangisTask){
        Calendar calendar = Calendar.getInstance();
        this.launchableExchangisTask = launchableExchangisTask;
        this.id = launchableExchangisTask.getId();
        this.name = launchableExchangisTask.getName();
        this.createTime = calendar.getTime();
        this.lastUpdateTime = calendar.getTime();
        this.engineType = launchableExchangisTask.getEngineType();
        this.executeUser = launchableExchangisTask.getExecuteUser();
        this.jobId = launchableExchangisTask.getJobId();
        // jobName
        this.jobExecutionId = launchableExchangisTask.getJobExecutionId();
        this.status = TaskStatus.Scheduled;
        this.lastUpdateTime = Calendar.getInstance().getTime();
    }
    public String getTaskId(){
        if (Objects.isNull(taskId) && Objects.nonNull(getId())){
            this.taskId = String.valueOf(getId());
        }
        return this.taskId;
    }

    public void setTaskId(String taskId){
        this.taskId = taskId;
    }

    public String getLinkisJobId() {
        return linkisJobId;
    }

    public void setLinkisJobId(String linkisJobId) {
        this.linkisJobId = linkisJobId;
    }

    public String getLinkisJobInfo() {
        if (Objects.isNull(this.linkisJobInfo) && Objects.nonNull(this.linkisJobInfoMap)){
            this.linkisJobInfo = Json.toJson(this.linkisJobInfoMap, null);
        }
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
        if (Objects.isNull(this.metrics) && Objects.nonNull(this.metricsMap)){
            this.metrics = Json.toJson(this.metricsMap, null);
        }
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getLinkisJobInfoMap() {
        if (Objects.isNull(this.linkisJobInfoMap) && Objects.nonNull(this.linkisJobInfo)){
            this.linkisJobInfoMap = Json.fromJson(this.linkisJobInfo, Map.class);
        }
        return linkisJobInfoMap;
    }

    public void setLinkisJobInfoMap(Map<String, Object> linkisJobInfoMap) {
        this.linkisJobInfoMap = linkisJobInfoMap;
    }

    public Map<String, Object> getMetricsMap() {
        if (Objects.isNull(this.metricsMap) && Objects.nonNull(this.metrics)){
            this.metricsMap = Json.fromJson(this.metrics, Map.class);
        }
        return metricsMap;
    }

    public void setMetricsMap(Map<String, Object> metricsMap) {
        this.metricsMap = metricsMap;
    }

    public LaunchableExchangisTask getLaunchableExchangisTask() {
        return launchableExchangisTask;
    }

    public void setLaunchableExchangisTask(LaunchableExchangisTask launchableExchangisTask) {
        this.launchableExchangisTask = launchableExchangisTask;
    }
}
