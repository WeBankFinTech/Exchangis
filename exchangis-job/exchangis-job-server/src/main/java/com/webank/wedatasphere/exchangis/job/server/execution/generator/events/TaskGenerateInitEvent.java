package com.webank.wedatasphere.exchangis.job.server.execution.generator.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;

/**
 * Init event
 */
public class TaskGenerateInitEvent extends TaskGenerateEvent{
    public TaskGenerateInitEvent(LaunchableExchangisJob launchableExchangisJob) {
        super(System.currentTimeMillis(), launchableExchangisJob);
    }
}
