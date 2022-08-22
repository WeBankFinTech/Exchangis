package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.AbstractTaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskDequeueEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskExecutionEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskDeleteEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskStatusUpdateEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobServerLogging;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Submit scheduler task
 */
public class SubmitSchedulerTask extends AbstractExchangisSchedulerTask implements JobServerLogging<String> {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitSchedulerTask.class);

    private LaunchableExchangisTask launchableExchangisTask;

    private TaskManager<LaunchedExchangisTask> taskManager;

    private ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> launcher;

    private TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer;

    private Callable<Boolean> submitCondition;

    private AtomicInteger retryCnt = new AtomicInteger(0);
    /**
     * Each schedule task should has an id
     *
     */
    public SubmitSchedulerTask(LaunchableExchangisTask task, Callable<Boolean> submitCondition) {
        super(String.valueOf(task.getId()));
        this.launchableExchangisTask = task;
        this.submitCondition = submitCondition;
    }

    public SubmitSchedulerTask(LaunchableExchangisTask task){
        this(task, null);
    }
    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        Boolean submitAble;
        String jobExecutionId = this.launchableExchangisTask.getJobExecutionId();
        try {
            submitAble = submitCondition.call();
        } catch (Exception e){
            throw new ExchangisSchedulerRetryException("Error occurred in examining submit condition for task: [" + launchableExchangisTask.getId() + "]", e);
        }
        if (submitAble) {
            info(jobExecutionId, "Submit the launchable task: [name:{} ,id:{} ] to launcher: [{}], retry_count: {}",
                    launchableExchangisTask.getName(), launchableExchangisTask.getId(), launcher.name(), retryCnt.get());
            LaunchedExchangisTask launchedExchangisTask;
            try {
                // Invoke launcher
                Date launchTime = Calendar.getInstance().getTime();
                launchedExchangisTask = launcher.launch(this.launchableExchangisTask);
//                launchedExchangisTask = new LaunchedExchangisTask(launchableExchangisTask);
                launchedExchangisTask.setLaunchTime(launchTime);
            } catch (Exception e) {
                if (retryCnt.incrementAndGet() < getMaxRetryNum()) {
                    // Remove the launched task stored
                    onEvent(new TaskDeleteEvent(String.valueOf(launchableExchangisTask.getId())));
                    throw new ExchangisSchedulerRetryException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() +"]", e);
                }else {
                    // Update the launched task status to fail
                    launchedExchangisTask = new LaunchedExchangisTask();
                    launchedExchangisTask.setTaskId(String.valueOf(launchableExchangisTask.getId()));
                    launchedExchangisTask.setJobExecutionId(launchableExchangisTask.getJobExecutionId());
                    onEvent(new TaskStatusUpdateEvent(launchedExchangisTask, TaskStatus.Failed));
                }
                throw new ExchangisSchedulerException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() +"]", e);
            }
            // Add the success/launched job into taskManager
            if (Objects.nonNull(this.taskManager)){
                boolean successAdd = true;
                try {
                    this.taskManager.addRunningTask(launchedExchangisTask);
                } catch (Exception e){
                    successAdd = false;
                    error(jobExecutionId, "Error occurred in adding running task: [{}] to taskManager, linkis_id: [{}], should kill the job in linkis!",
                            launchedExchangisTask.getId(), launchedExchangisTask.getLinkisJobId(), e);
                    LaunchedExchangisTask finalLaunchedExchangisTask1 = launchedExchangisTask;
                    Optional.ofNullable(launchedExchangisTask.getLauncherTask()).ifPresent(launcherTask -> {
                        try {
                            launcherTask.kill();
                        } catch (ExchangisTaskLaunchException ex){
                            LOG.error("Kill linkis_id: [{}] fail", finalLaunchedExchangisTask1.getLinkisJobId(), e);
                        }
                    });
                }
                if (successAdd){
                    try {
                        onEvent(new TaskDequeueEvent(launchableExchangisTask.getId() + ""));
                    }catch (Exception e){
                        // Ignore the exception
                        LOG.warn("Fail to dequeue the launchable task [{}]", launchableExchangisTask.getId(), e);
                    }
                    if (Objects.nonNull(this.loadBalancer)){
                        // Add the launchedExchangisTask to the load balance poller
                        List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> loadBalanceSchedulerTasks = this.loadBalancer.choose(launchedExchangisTask);
                        LaunchedExchangisTask finalLaunchedExchangisTask = launchedExchangisTask;
                        Optional.ofNullable(loadBalanceSchedulerTasks).ifPresent(tasks -> tasks.forEach(loadBalanceSchedulerTask -> {
                            loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().push(finalLaunchedExchangisTask);
                        }));
                    }
                }
            }
        }
    }

    /**
     * Listen the execution event
     * @param event
     */
    private void onEvent(TaskExecutionEvent event){
        if (this.taskManager instanceof AbstractTaskManager) {
            ((AbstractTaskManager) this.taskManager).onEvent(event);
        }
    }

    @Override
    public JobLogEvent getJobLogEvent(JobLogEvent.Level level, String executionId, String message, Object... args) {
        return new JobLogEvent(level, this.getTenancy(), executionId, message, args);
    }

    @Override
    public JobLogListener getJobLogListener() {
        if (Objects.nonNull(this.taskManager)){
            return this.taskManager.getJobLogListener();
        }
        return null;
    }

    @Override
    public String getName() {
        return "Scheduler-SubmitTask-" + getId();
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }

    public TaskManager<LaunchedExchangisTask> getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager<LaunchedExchangisTask> taskManager) {
        this.taskManager = taskManager;
    }

    public ExchangisTaskLauncher<LaunchableExchangisTask,LaunchedExchangisTask> getLauncher() {
        return launcher;
    }

    public void setLauncher(ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> launcher) {
        this.launcher = launcher;
    }

    public TaskSchedulerLoadBalancer<LaunchedExchangisTask> getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
}
