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
    private Date version;
    public TaskPrepareEvent(String taskId, Date version) {
        super(null);
        this.taskId = taskId;
        this.version = version;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }
}
