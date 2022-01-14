package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;

/**
 * Task execution
 */
public interface TaskExecution {


    /**
     * Submit scheduler task
     * @param schedulerTask scheduler task
     * @return scheduleId
     */
    String submit(ExchangisSchedulerTask schedulerTask) throws ExchangisTaskExecuteException;

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
