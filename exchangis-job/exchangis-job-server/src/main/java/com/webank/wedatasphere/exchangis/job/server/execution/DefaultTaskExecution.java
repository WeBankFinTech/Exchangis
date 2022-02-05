package com.webank.wedatasphere.exchangis.job.server.execution;


import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskExecuteException;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.MetricUpdateSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.StatusUpdateSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.SubmitSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskChooseRuler;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.TaskObserver;
import org.apache.linkis.scheduler.Scheduler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default task execution
 */
public class DefaultTaskExecution extends AbstractTaskExecution{

    public static final String DEFAULT_LAUNCHER_NAME = "Linkis";
    /**
     * Scheduler
     */
    private Scheduler scheduler;

    /**
     * TaskManager
     */
    private TaskManager<LaunchedExchangisTask> taskManager;

    /**
     * Observer list
     */
    private List<TaskObserver<?>> taskObservers;

    private TaskChooseRuler<LaunchableExchangisTask> taskChooseRuler;
    /**
     * load balancer
     */
    private TaskSchedulerLoadBalancer<LaunchedExchangisTask> taskSchedulerLoadBalancer;

    /**
     * Launch manager
     */
    private ExchangisTaskLaunchManager launchManager;
    /**
     *
     * @param scheduler scheduler
     * @param launchManager launch manager
     * @param taskManager task manager for launched task
     * @param taskObservers task observers
     * @param taskSchedulerLoadBalancer load balancer
     * @param taskChooseRuler choose ruler
     */
    public DefaultTaskExecution(Scheduler scheduler, ExchangisTaskLaunchManager launchManager,
                                TaskManager<LaunchedExchangisTask> taskManager, List<TaskObserver<?>> taskObservers,
                                TaskSchedulerLoadBalancer<LaunchedExchangisTask> taskSchedulerLoadBalancer,
                                TaskChooseRuler<LaunchableExchangisTask> taskChooseRuler){
        this.scheduler = scheduler;
        this.taskManager = taskManager;
        this.taskObservers = taskObservers;
        this.launchManager = launchManager;
        this.taskSchedulerLoadBalancer = taskSchedulerLoadBalancer;
        this.taskChooseRuler = taskChooseRuler;
    }

    @Override
    protected synchronized void init() throws ExchangisTaskExecuteException {
        super.init();
        Optional.ofNullable(getTaskSchedulerLoadBalancer()).ifPresent(loadBalancer -> {
            loadBalancer.registerSchedulerTask(StatusUpdateSchedulerTask.class);
            loadBalancer.registerSchedulerTask(MetricUpdateSchedulerTask.class);
        });
    }

    @Override
    public void preSubmit(ExchangisSchedulerTask schedulerTask) {
        if (schedulerTask instanceof SubmitSchedulerTask){
            SubmitSchedulerTask submitSchedulerTask = ((SubmitSchedulerTask) schedulerTask);
            if (Objects.nonNull(getExchangisLaunchManager())){
                submitSchedulerTask.setLauncher(getExchangisLaunchManager().getTaskLauncher(DEFAULT_LAUNCHER_NAME));
            }
            submitSchedulerTask.setTaskManager(getTaskManager());
            submitSchedulerTask.setLoadBalancer(getTaskSchedulerLoadBalancer());
        }
    }

    @Override
    protected TaskManager<LaunchedExchangisTask> getTaskManager() {
        return this.taskManager;
    }

    @Override
    protected List<TaskObserver<?>> getTaskObservers() {
        return this.taskObservers;
    }

    @Override
    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    protected TaskChooseRuler<LaunchableExchangisTask> getTaskChooseRuler() {
        return taskChooseRuler;
    }

    @Override
    protected ExchangisTaskLaunchManager getExchangisLaunchManager() {
        return launchManager;
    }

    @Override
    protected TaskSchedulerLoadBalancer<LaunchedExchangisTask> getTaskSchedulerLoadBalancer() {
        return this.taskSchedulerLoadBalancer;
    }

}
