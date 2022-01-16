package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;

/**
 * Task execution
 */
public interface TaskExecution<T extends ExchangisTask> {

    void submit(T task) throws ExchangisTaskExecuteException;
    /**
     * Submit scheduler task
     * @param schedulerTask scheduler task
     */
    void submit(AbstractExchangisSchedulerTask schedulerTask) throws ExchangisSchedulerException;

    /**
     * Start execution
     */
    void start();

    /**
     * Stop execution
     */
    void stop();

    void addListener(TaskExecutionListener listener);
}
