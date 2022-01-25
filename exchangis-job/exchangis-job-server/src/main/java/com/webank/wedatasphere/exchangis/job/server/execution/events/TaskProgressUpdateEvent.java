package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;

/**
 * Update of task progress
 */
public class TaskProgressUpdateEvent extends TaskExecutionEvent{

    private TaskProgressInfo progressInfo;

    public TaskProgressUpdateEvent(LaunchedExchangisTask task, TaskProgressInfo progressInfo){
        super(task);
        this.progressInfo = progressInfo;
    }

    public TaskProgressInfo getProgressInfo() {
        return progressInfo;
    }

    public void setProgressInfo(TaskProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
    }
}
