package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;

public class DefaultTaskExecution extends AbstractTaskExecution{

    public DefaultTaskExecution(Scheduler scheduler, ExchangisJobLaunchManager<?> launchManager,
                                TaskManager<LaunchedExchangisTask> taskManager, List<TaskObserver<?>> taskObservers,
                                TaskSchedulerLoadBalancer<LaunchedExchangisTask> taskSchedulerLoadBalancer){

    }
    @Override
    protected TaskManager<LaunchedExchangisTask> getTaskManager() {
        return null;
    }

    @Override
    protected List<TaskObserver<?>> getTaskObservers() {
        return null;
    }

    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected ExchangisJobLaunchManager<?> getExchangisJobLaunchManager() {
        return null;
    }

    @Override
    protected TaskSchedulerLoadBalancer<LaunchedExchangisTask> getTaskSchedulerLoadBalancer() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addListener(TaskExecutionListener listener) {

    }
}
