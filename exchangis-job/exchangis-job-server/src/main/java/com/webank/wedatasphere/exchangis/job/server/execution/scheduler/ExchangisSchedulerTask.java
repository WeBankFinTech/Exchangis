package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.scheduler.queue.SchedulerEvent;

/**
 * Exchangis scheduler task
 */
public interface ExchangisSchedulerTask extends SchedulerEvent {

    /**
     * Tenancy
     * @return
     */
    String getTenancy();

    void setTenancy(String tenancy);

}
