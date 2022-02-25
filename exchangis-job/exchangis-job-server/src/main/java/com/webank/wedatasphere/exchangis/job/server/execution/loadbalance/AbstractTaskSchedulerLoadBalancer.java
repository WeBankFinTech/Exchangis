package com.webank.wedatasphere.exchangis.job.server.execution.loadbalance;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.LoadBalanceSchedulerTask;
import com.webank.wedatasphere.exchangis.job.utils.TypeGenericUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Scheduler load balancer for launched task
 */
public abstract class AbstractTaskSchedulerLoadBalancer implements TaskSchedulerLoadBalancer<LaunchedExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskSchedulerLoadBalancer.class);

    protected TaskManager<LaunchedExchangisTask> taskManager;

    protected Scheduler scheduler;

    protected List<Class<?>> registeredTaskClasses = new ArrayList<>();



    public AbstractTaskSchedulerLoadBalancer(Scheduler scheduler, TaskManager<LaunchedExchangisTask> taskManager){
        this.taskManager = taskManager;
        this.scheduler = scheduler;
    }
    @Override
    public TaskManager<LaunchedExchangisTask> getTaskManager() {
        return this.taskManager;
    }

    @Override
    public void registerSchedulerTask(Class<?> schedulerTaskClass){
        if(isSuitableClass(schedulerTaskClass)){
            LOG.info("Register the load balance scheduler class: [{}]", schedulerTaskClass.getName());
            registeredTaskClasses.add(schedulerTaskClass);
        }
    }

    @Override
    public List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> choose(LaunchedExchangisTask launchedExchangisTask) {
        List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> schedulerTasks = new ArrayList<>();
        registeredTaskClasses.forEach(taskClass -> {
            Optional.ofNullable(choose(launchedExchangisTask, taskClass, false)).ifPresent(schedulerTasks::add);
        });
        return schedulerTasks;
    }

    @Override
    public LoadBalanceSchedulerTask<LaunchedExchangisTask> choose(LaunchedExchangisTask launchedExchangisTask, Class<?> schedulerTaskClass) {
        return choose(launchedExchangisTask, schedulerTaskClass, false);
    }


    /**
     *  Choose entrance
     * @param launchedExchangisTask task
     * @param schedulerTaskClass task class
     * @param unchecked if checked
     * @return scheduler task
     */
    protected abstract LoadBalanceSchedulerTask<LaunchedExchangisTask> choose(LaunchedExchangisTask launchedExchangisTask, Class<?> schedulerTaskClass, boolean unchecked);


    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected boolean isSuitableClass(Class<?> schedulerTaskClass){
        if (LoadBalanceSchedulerTask.class.isAssignableFrom(schedulerTaskClass)){
            Class<?> subType =  TypeGenericUtils.getActualTypeFormGenericClass(schedulerTaskClass, null, 0);
            if (Objects.isNull(subType) || !subType.equals(LaunchedExchangisTask.class)){
                LOG.warn("Unrecognized generic sub type: [{}] in scheduler", subType);
            } else {
                return true;
            }
        } else {
            LOG.warn("Not load balance scheduler task class [{}]", schedulerTaskClass.getCanonicalName());
        }
        return false;
    }

}
