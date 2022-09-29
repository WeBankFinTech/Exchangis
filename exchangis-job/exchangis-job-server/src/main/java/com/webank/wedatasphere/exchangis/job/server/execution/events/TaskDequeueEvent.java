package com.webank.wedatasphere.exchangis.job.server.execution.events;


/**
 * Event that remove the launchable task from the queue(table)
 */
public class TaskDequeueEvent extends TaskExecutionEvent{
    /**
     * Task id
     */
    private String taskId;
    /**
     * @param taskId task id
     */
    public TaskDequeueEvent(String taskId) {
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
