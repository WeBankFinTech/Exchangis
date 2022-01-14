package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;

import java.util.List;

public class DefaultGenericTaskExecution extends AbstractTaskExecution{

    public DefaultGenericTaskExecution(TaskScheduler taskScheduler, ExchangisJobLaunchManager<?> launchManager,
                                       TaskManager<LaunchedExchangisTask> taskManager, List<TaskObserver<?>> taskObservers){

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
    protected TaskScheduler getTaskScheduler() {
        return null;
    }

    @Override
    protected ExchangisJobLaunchManager<?> getExchangisJobLaunchManager() {
        return null;
    }

    @Override
    public String submit(ExchangisSchedulerTask schedulerTask){
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
