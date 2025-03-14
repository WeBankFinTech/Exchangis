package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;

/**
 * Task execution
 */
public interface TaskExecution<T extends ExchangisTask> {

    void submit(T task) throws ExchangisTaskExecuteException;
    /**
     * Submit scheduler task
     * @param schedulerTask scheduler task
     */
    void submit(ExchangisSchedulerTask schedulerTask) throws ExchangisSchedulerException;

    /**
     * Start execution
     */
    void start() throws ExchangisTaskExecuteException;

    /**
     * Stop execution
     */
    void stop();

    void addListener(TaskExecutionListener listener);

}
