package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.scheduler.AbstractScheduler;
import org.apache.linkis.scheduler.SchedulerContext;

/**
 * Inherited the AbstractScheduler from linkis-scheduler
 */
public class ExchangisTaskScheduler extends AbstractScheduler {
    @Override
    public String getName() {
        return "Exchangis-Multi-Tenancy-Scheduler";
    }

    @Override
    public SchedulerContext getSchedulerContext() {
        return null;
    }
}
