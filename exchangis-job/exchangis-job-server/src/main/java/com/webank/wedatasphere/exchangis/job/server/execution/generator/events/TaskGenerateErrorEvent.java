package com.webank.wedatasphere.exchangis.job.server.execution.generator.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;

/**
 * Error event
 */
public class TaskGenerateErrorEvent extends TaskGenerateEvent{

    private Throwable exception;

    public TaskGenerateErrorEvent(LaunchableExchangisJob launchableExchangisJob, Throwable e) {
        super(System.currentTimeMillis(), launchableExchangisJob);
        this.exception = e;
    }

    public Throwable getException() {
        return exception;
    }

}
