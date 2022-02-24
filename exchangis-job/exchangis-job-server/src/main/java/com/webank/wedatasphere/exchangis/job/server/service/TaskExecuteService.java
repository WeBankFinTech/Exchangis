package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecutionListener;

import java.util.Date;

/**
 * Task execute service
 */
public interface TaskExecuteService extends TaskExecutionListener {

    /**
     * Update the task and its related job status
     * @param task task
     * @param status status
     */
    void updateTaskStatus(LaunchedExchangisTask task, TaskStatus status, boolean updateJob) throws ExchangisOnEventException;

    void updateTaskProgress(LaunchedExchangisTask task, float progress) throws ExchangisOnEventException;

    /**
     * Try to update the job progress by executionId
     * @param jobExecutionId job execution id
     */
    void updateJobProgress(String jobExecutionId, Date updateTime);
    /**
     * Try to update the job status by executionId
     * @param jobExecutionId job execution id
     * @param status status
     */
    void updateJobStatus(String jobExecutionId, TaskStatus status, Date updateTime);
}
