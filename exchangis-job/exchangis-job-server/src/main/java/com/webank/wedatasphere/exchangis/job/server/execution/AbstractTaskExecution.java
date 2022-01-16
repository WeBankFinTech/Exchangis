package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.SubmitSchedulerTaskAbstract;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;

/**
 * Contains:
 * 1) TaskManager to manager running task.
 * 2) TaskObserver to observe initial task.
 * 3) TaskScheduler to submit scheduler task.
 */
public abstract class AbstractTaskExecution implements TaskExecution<LaunchableExchangisTask> {

    @Override
    public void submit(LaunchableExchangisTask task) throws ExchangisTaskExecuteException{
        SubmitSchedulerTaskAbstract submitSchedulerTask = new SubmitSchedulerTaskAbstract(task);

    }

    @Override
    public void submit(AbstractExchangisSchedulerTask schedulerTask) throws ExchangisSchedulerException {

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

    /**
     * TaskManager of launchedExchangisTask
     * @return task Manager
     */
    protected abstract  TaskManager<LaunchedExchangisTask> getTaskManager();

    /**
     * TaskObserver
     * @return list
     */
    protected abstract List<TaskObserver<?>> getTaskObservers();

    /**
     * Scheduler
     * @return Scheduler
     */
    protected abstract Scheduler getScheduler();


    /**
     * Launch manager
     * @return launch manager
     */
    protected abstract ExchangisJobLaunchManager<?> getExchangisJobLaunchManager();

    protected abstract TaskSchedulerLoadBalancer<LaunchedExchangisTask> getTaskSchedulerLoadBalancer();
}
