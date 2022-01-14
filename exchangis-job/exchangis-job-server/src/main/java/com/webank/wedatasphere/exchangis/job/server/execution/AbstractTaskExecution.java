package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.launcher.ExchangisJobLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;

import java.util.List;

/**
 * Contains:
 * 1) TaskManager to manager running task.
 * 2) TaskObserver to observe initial task.
 * 3) TaskScheduler to submit scheduler task.
 */
public abstract class AbstractTaskExecution implements TaskExecution {

    @Override
    public String submit(ExchangisSchedulerTask schedulerTask) throws ExchangisTaskExecuteException {

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

    /**
     * TaskManager
     * @return task Manager
     */
    protected abstract  TaskManager<LaunchedExchangisTask> getTaskManager();

    /**
     * TaskObserver
     * @return list
     */
    protected abstract List<TaskObserver<?>> getTaskObservers();

    /**
     * TaskScheduler
     * @return task scheduler
     */
    protected abstract TaskScheduler getTaskScheduler();


    /**
     * Launch manager
     * @return launch manager
     */
    protected abstract ExchangisJobLaunchManager<?> getExchangisJobLaunchManager();
}
