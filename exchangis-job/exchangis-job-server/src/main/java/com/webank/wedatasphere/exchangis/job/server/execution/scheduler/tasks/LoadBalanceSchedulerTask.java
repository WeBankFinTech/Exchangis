package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;

/**
 * Scheduler task could be balanced,
 * Each one is a resident task with a poller
 * @param <T>
 */
public interface LoadBalanceSchedulerTask<T> extends ExchangisSchedulerTask {

    /**
     * Get/Create a poller
     * @return
     */
    LoadBalancePoller<T> getOrCreateLoadBalancePoller();
}
