package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.priority.PriorityRunnable;

/**
 * Define the basic interface of thread in scheduler
 */
public interface SchedulerThread extends PriorityRunnable {
    /**
     * Start entrance
     */
    void start();

    /**
     * Stop entrance
     */
    void stop();

    /**
     * Name
     * @return
     */
    String getName();
}
