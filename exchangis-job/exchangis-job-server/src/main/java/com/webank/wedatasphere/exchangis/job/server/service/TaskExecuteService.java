package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecutionListener;

/**
 * Task execute service
 */
public interface TaskExecuteService extends TaskExecutionListener {

    /**
     * Update the task and its related job status)
     * @param task task
     * @param status status
     */
    void updateTaskStatus(LaunchedExchangisTask task, TaskStatus status);
}
