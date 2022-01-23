package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

/**
 * Insert event
 */
public class TaskInfoUpdateEvent extends TaskExecutionEvent{

    public TaskInfoUpdateEvent(LaunchedExchangisTask task) {
        super(task);
    }
}
