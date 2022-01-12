package com.webank.wedatasphere.exchangis.job.server.execution;


import org.apache.linkis.scheduler.Scheduler;

/**
 * Contains:
 * 1) TaskManager to manager running task.
 * 2) TaskObserver to observe initial task.
 * 3) TaskScheduler to submit scheduler task.
 */
public abstract class AbstractTaskExecution implements TaskExecution {
    Scheduler getTaskScheduler() {
        Scheduler scheduler = null;
        return null;
    }
}
