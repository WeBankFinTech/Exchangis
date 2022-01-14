package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance;

import java.util.List;

public interface LoadBalancePoller<T> {

    List<T> poll();

    double pollFreqPerSec();

    void setLoadBalanceRule(LoadBalanceRule<T> rule);
}
