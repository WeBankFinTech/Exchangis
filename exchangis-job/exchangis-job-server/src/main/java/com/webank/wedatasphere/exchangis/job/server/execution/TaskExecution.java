package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import org.apache.linkis.common.exception.ErrorException;

/**
 * Task execution
 */
public interface TaskExecution {

    /**
     * Submit scheduler task
     * @param schedulerTask scheduler task
     * @return scheduleId
     */
    String submit(ExchangisSchedulerTask schedulerTask) throws ErrorException;

    /**
     * Start execution
     */
    void start();

    /**
     * Stop execution
     */
    void stop();
}
