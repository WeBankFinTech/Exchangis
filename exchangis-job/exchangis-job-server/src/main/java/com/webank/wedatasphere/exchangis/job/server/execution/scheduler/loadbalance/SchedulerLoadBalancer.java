package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.LoadBalanceSchedulerTask;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;


/**
 * load balancer api of scheduler
 */
public interface SchedulerLoadBalancer<T> {
    /**
     * Register the scheduler task into the balancer
     * @param schedulerTaskClass class of scheduler task
     */
    void registerSchedulerTask(Class<?> schedulerTaskClass);

    /**
     * Choose the load balance scheduler tasks
     * @param element task
     * @return
     */
    List<LoadBalanceSchedulerTask<T>> choose(T element);

    LoadBalanceSchedulerTask<T> choose(T element, Class<?> schedulerTaskClass);
    /**
     * Hold the scheduler to analyze the condition of loading
     * @return
     */
    Scheduler getScheduler();
}
