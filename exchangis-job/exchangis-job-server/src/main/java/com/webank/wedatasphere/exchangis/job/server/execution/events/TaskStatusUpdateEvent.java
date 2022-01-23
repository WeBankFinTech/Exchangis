package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

/**
 * Updating of task status
 */
public class TaskStatusUpdateEvent extends TaskExecutionEvent{

    private TaskStatus updateStatus;

    public TaskStatusUpdateEvent(LaunchedExchangisTask task, TaskStatus status) {
        super(task);
        this.updateStatus = status;
    }

    public TaskStatus getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(TaskStatus updateStatus) {
        this.updateStatus = updateStatus;
    }
}
