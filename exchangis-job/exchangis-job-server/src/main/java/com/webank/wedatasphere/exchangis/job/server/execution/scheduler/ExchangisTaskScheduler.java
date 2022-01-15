package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.AbstractScheduler;
import org.apache.linkis.scheduler.SchedulerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Inherited the AbstractScheduler from linkis-scheduler
 */
@Component
public class ExchangisTaskScheduler extends AbstractScheduler {
    private SchedulerContext schedulerContext;
//    private static final CommonVars<String> COMMON_VARS = CommonVars.apply("/", "");
    @Override
    @PostConstruct
    public void init() {
        this.schedulerContext = new ExchangisSchedulerContext(1, "");
    }

    @Override
    public String getName() {
        return "Exchangis-Multi-Tenancy-Scheduler";
    }

    @Override
    public SchedulerContext getSchedulerContext() {
        return schedulerContext;
    }
}
