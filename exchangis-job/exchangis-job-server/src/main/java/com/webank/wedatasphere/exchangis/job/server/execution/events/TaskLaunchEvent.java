package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

/**
 * Insert event
 */
public class TaskLaunchEvent extends TaskExecutionEvent{

    public TaskLaunchEvent(LaunchedExchangisTask task) {
        super(task);
    }
}
