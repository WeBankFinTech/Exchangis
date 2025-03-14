package com.webank.wedatasphere.exchangis.job.server.execution.events;


import java.util.Date;

/**
 * Event that prepare task for execution/submit
 */
public class TaskPrepareEvent extends TaskExecutionEvent{

    /**
     * Task id
     */
    private String taskId;

    /**
     * Version date
     */
    private Integer version;

    /**
     * Update time
     */
    private Date updateTime;
    public TaskPrepareEvent(String taskId, Integer version, Date updateTime) {
        super(null);
        this.taskId = taskId;
        this.version = version;
        this.updateTime = updateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    @Override
    public String eventId() {
        return "_TaskExecution_" + this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
