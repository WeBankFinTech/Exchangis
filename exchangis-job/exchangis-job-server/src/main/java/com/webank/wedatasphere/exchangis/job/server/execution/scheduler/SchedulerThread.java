package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

/**
 * Define the basic interface of thread in scheduler
 */
public interface SchedulerThread extends Runnable{
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
