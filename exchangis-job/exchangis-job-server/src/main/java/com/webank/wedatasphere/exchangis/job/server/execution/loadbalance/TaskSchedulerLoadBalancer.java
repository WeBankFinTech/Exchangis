package com.webank.wedatasphere.exchangis.job.server.execution.loadbalance;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.SchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;

public interface TaskSchedulerLoadBalancer<T extends ExchangisTask> extends SchedulerLoadBalancer<T> {

    /**
     * Manager the running tasks
     * @return task manager
     */
    TaskManager<T> getTaskManager();

}
