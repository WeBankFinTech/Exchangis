package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.exchangis.job.launcher.domain.TaskStatus;

import java.util.Map;

/**
 * Define the operation method of launched task
 */
public interface AccessibleLaunchedExchangisTask {

    /**
     * Call the metric interface
     * @return map
     */
    Map<String, Object> callMetricsUpdate();

    /**
     * Call the progress interface
     * @return double
     */
    TaskStatus callStatusUpdate();

    /**
     * Kill the task
     */
    void kill();
}
