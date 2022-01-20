package com.webank.wedatasphere.exchangis.job.server.execution.events;

public class TaskInfoDeleteEvent extends TaskExecutionEvent {

    private String taskId;

    public TaskInfoDeleteEvent(String taskId) {
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
