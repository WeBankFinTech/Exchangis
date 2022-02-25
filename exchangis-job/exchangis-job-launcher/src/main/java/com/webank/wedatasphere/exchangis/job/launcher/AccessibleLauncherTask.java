package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

import java.util.Map;

/**
 * Define the operation method of launched task
 */
public interface AccessibleLauncherTask {


    /**
     * Call the status interface
     * @return task status
     * @throws ExchangisTaskLaunchException
     */
    TaskStatus getStatus() throws ExchangisTaskLaunchException;

    TaskStatus getLocalStatus();
    /**
     * Call the metric interface
     * @return map
     */
    Map<String, Object> getMetricsInfo() throws ExchangisTaskLaunchException;

    /**
     * Call the progress info interface
     * @return double
     */
    TaskProgressInfo getProgressInfo() throws ExchangisTaskLaunchException;

    /**
     * Kill the task
     */
    void kill() throws ExchangisTaskLaunchException;

    /**
     * Query log
     * @param query query
     * @return
     */
    LogResult queryLogs(LogQuery query) throws ExchangisTaskLaunchException;

    /**
     * Submit
     * @throws ExchangisTaskLaunchException exception
     */
    void submit() throws ExchangisTaskLaunchException;
}
