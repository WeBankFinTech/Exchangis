package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance;

import java.util.List;

/**
 * Poller for load balance
 * @param <T>
 */
public interface LoadBalancePoller<T> {

    List<T> poll() throws InterruptedException;

    /**
     * Push the element
     * @param element element
     */
    boolean push(T element);

    /**
     * Combine with other poller
     * @param other
     */
    void combine(LoadBalancePoller<T> other);

    int size();

    /**
     * Close the poller
     */
    void close();
}
