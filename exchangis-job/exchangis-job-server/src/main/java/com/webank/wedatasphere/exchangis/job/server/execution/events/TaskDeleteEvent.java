package com.webank.wedatasphere.exchangis.job.server.execution.events;

public class TaskDeleteEvent extends TaskExecutionEvent {

    private String taskId;

    public TaskDeleteEvent(String taskId) {
        super(null);
        this.taskId = taskId;
    }

    @Override
    public String eventId() {
        return "_TaskExecution_" + this.taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
