package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.priority.PriorityRunnable;
import org.apache.linkis.scheduler.queue.SchedulerEvent;

/**
 * Exchangis scheduler task
 */
public interface ExchangisSchedulerTask extends PriorityRunnable, SchedulerEvent {

    /**
     * Tenancy
     * @return
     */
    String getTenancy();

    void setTenancy(String tenancy);

}
