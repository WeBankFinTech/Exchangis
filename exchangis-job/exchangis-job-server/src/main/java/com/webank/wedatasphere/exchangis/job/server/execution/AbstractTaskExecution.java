package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.AbstractLoadBalanceSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.SubmitSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskChooseRuler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import com.webank.wedatasphere.exchangis.job.utils.TypeGenericUtils;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains:
 * 1) TaskManager to manager running task.
 * 2) TaskObserver to observe initial task.
 * 3) TaskScheduler to submit scheduler task.
 */
public abstract class AbstractTaskExecution implements TaskExecution<LaunchableExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskExecution.class);
    private boolean initial = false;

    /**
     * Execution listeners
     */
    private List<TaskExecutionListener> listeners = new ArrayList<>();

    @Override
    public void submit(LaunchableExchangisTask task) throws ExchangisTaskExecuteException{
        SubmitSchedulerTask submitSchedulerTask = new SubmitSchedulerTask(task);
        try {
            submit(submitSchedulerTask);
        } catch (ExchangisSchedulerException e) {
            throw new ExchangisTaskExecuteException("Submit task [" + task.getId() + "] to schedule occurred error", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(ExchangisSchedulerTask schedulerTask) throws ExchangisSchedulerException {
        try{
            preSubmit(schedulerTask);
            if (schedulerTask instanceof AbstractLoadBalanceSchedulerTask){
                try {
                    ((AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask>) schedulerTask)
                            .setSchedulerLoadBalancer(getTaskSchedulerLoadBalancer());
                }catch (Exception e){
                    //Ignore the exception
                    LOG.warn("Load balance scheduler task [" + schedulerTask.getClass().getSimpleName() + "] doesn't match the load balancer", e);
                }
            }
            getScheduler().submit(schedulerTask);
        }catch (Exception e){
            throw new ExchangisSchedulerException("Submit scheduler task [id: " + schedulerTask.getId() + ", type: " + schedulerTask.getClass().getName() + "] occurred error", e);
        }
    }

    @Override
    public void start() throws ExchangisTaskExecuteException {
        if (!initial){
            init();
        }
        // Start the scheduler
        getScheduler().start();
        // Start the observers
        Optional.ofNullable(getTaskObservers()).ifPresent(taskObservers -> taskObservers.forEach(TaskObserver::start));
        // Start the loadBalancer
        TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer = getTaskSchedulerLoadBalancer();
        if (Objects.nonNull(loadBalancer) && loadBalancer instanceof SchedulerThread){
            ((SchedulerThread) loadBalancer).start();
        }
    }

    @Override
    public void stop() {
        // Stop the observers
        Optional.ofNullable(getTaskObservers()).ifPresent(taskObservers -> taskObservers.forEach(TaskObserver::stop));
        // Stop the loadBalancer
        TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer = getTaskSchedulerLoadBalancer();
        if (Objects.nonNull(loadBalancer) && loadBalancer instanceof SchedulerThread){
            ((SchedulerThread) loadBalancer).stop();
        }
        // Stop the scheduler
        getScheduler().shutdown();
    }


    @SuppressWarnings("unchecked")
    protected synchronized void init() throws ExchangisTaskExecuteException{
        if (!initial){
            Scheduler scheduler = getScheduler();
            if (Objects.isNull(scheduler)){
                throw new ExchangisTaskExecuteException("Scheduler cannot be empty in task execution", null);
            }
            TaskManager<LaunchedExchangisTask> taskManager = getTaskManager();
            if (Objects.nonNull(taskManager) && taskManager instanceof  AbstractTaskManager){
                ((AbstractTaskManager) taskManager).setExecutionListener(new CombinedTaskExecutionListener());
            }
            List<TaskObserver<?>> observers = getTaskObservers();
            Optional.ofNullable(observers).ifPresent(taskObservers -> taskObservers.forEach(observer -> {
                observer.setScheduler(scheduler);
                Class<?> subType = TypeGenericUtils.getActualTypeFormGenericClass(observer.getClass(), null, 0);
                if (LaunchedExchangisTask.class.equals(subType)){
                    ((TaskObserver<LaunchedExchangisTask>)observer).setTaskManager(taskManager);
                } else if (LaunchableExchangisTask.class.equals(subType)){
                    ((TaskObserver<LaunchableExchangisTask>)observer).setTaskExecution(this);
                    ((TaskObserver<LaunchableExchangisTask>)observer).setTaskChooseRuler(getTaskChooseRuler());
                }
            }));
            initial = true;
        }
    }

    @Override
    public void addListener(TaskExecutionListener listener) {
        this.listeners.add(listener);
    }

    private class CombinedTaskExecutionListener implements TaskExecutionListener{

        @Override
        public void onEvent(TaskExecutionEvent taskExecutionEvent) throws ExchangisOnEventException {
            for(TaskExecutionListener listener : listeners){
                listener.onEvent(taskExecutionEvent);
            }
        }

        @Override
        public void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent) {
            // Ignore
        }

        @Override
        public void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent) {
            // Ignore
        }

        @Override
        public void onLaunch(TaskLaunchEvent infoUpdateEvent) {
            // Ignore
        }

        @Override
        public void onDelete(TaskDeleteEvent deleteEvent) {
            // Ignore
        }

        @Override
        public void onDequeue(TaskDequeueEvent dequeueEvent) throws ExchangisOnEventException {
            //Ignore
        }

        @Override
        public void onProgressUpdate(TaskProgressUpdateEvent updateEvent) {
            // Ignore
        }
    }

    /**
     * Pre hook of submitting
     * @param schedulerTask scheduler task
     */
    public void preSubmit(ExchangisSchedulerTask schedulerTask){
        // Do nothing
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

    protected abstract TaskChooseRuler<LaunchableExchangisTask> getTaskChooseRuler();
    /**
     * Launch manager
     * @return launch manager
     */
    protected abstract ExchangisTaskLaunchManager getExchangisLaunchManager();

    protected abstract TaskSchedulerLoadBalancer<LaunchedExchangisTask> getTaskSchedulerLoadBalancer();

}
