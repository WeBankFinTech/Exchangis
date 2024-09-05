package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitNoLeftException;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.AbstractTaskManager;
import com.webank.wedatasphere.exchangis.job.server.log.JobServerLogging;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskDequeueEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskExecutionEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.events.TaskPrepareEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * Submit scheduler task
 */
public class SubmitSchedulerTask extends AbstractExchangisSchedulerTask implements JobServerLogging<String> {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitSchedulerTask.class);

    private RateLimitService rateLimitService;

    private LaunchableExchangisTask launchableExchangisTask;

    private TaskManager<LaunchedExchangisTask> taskManager;

    private ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> launcher;

    private TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer;

    private Callable<Boolean> submitCondition;

    /**
     * Submit callback
     */
    private BiConsumer<SubmitSchedulerTask, Throwable> submitCallback;

    private AtomicInteger retryCnt = new AtomicInteger(0);

    /**
     * Submittable
     */
    private AtomicBoolean submitAble = new AtomicBoolean(false);

    /**
     * Each schedule task should has an id
     *
     */
    public SubmitSchedulerTask(RateLimitService rateLimitService, LaunchableExchangisTask task,
                               Callable<Boolean> submitCondition, BiConsumer<SubmitSchedulerTask, Throwable> submitCallback) {
        this(rateLimitService, task, submitCondition, submitCallback, false);
    }

    public SubmitSchedulerTask(RateLimitService rateLimitService, LaunchableExchangisTask task){
        this(rateLimitService, task, null, null, false);
    }
    public SubmitSchedulerTask(RateLimitService rateLimitService, LaunchableExchangisTask task,
                               Callable<Boolean> submitCondition, BiConsumer<SubmitSchedulerTask, Throwable> submitCallback,
                               boolean checkCondition) {
        super(String.valueOf(task.getId()));
        this.rateLimitService = rateLimitService;
        this.launchableExchangisTask = task;
        this.submitCondition = submitCondition;
        this.submitCallback = submitCallback;
        if (checkCondition) {
            try {
                submitAble.set(submitCondition.call());
            } catch (Exception e) {
                // Ignore
            }
        }
        // Set max retry
        setMaxRetryNum(2);
    }
    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        String jobExecutionId = this.launchableExchangisTask.getJobExecutionId();
        if (!submitAble.get()) {
            try {
                submitAble.set(submitCondition.call());
            } catch (Exception e) {
                throw new ExchangisSchedulerRetryException("Error occurred in examining submit condition for task: [" + launchableExchangisTask.getId() + "]", e);
            }
        }
        if (submitAble.get()) {
            Throwable submitExp = null;
            LaunchedExchangisTask launchedExchangisTask = null;
            boolean rateApply = false;
            try {
                rateApply = rateLimitService.rateLimit(this.launchableExchangisTask.getRateParams(), this.launchableExchangisTask.getRateParamsMap());
                info(jobExecutionId, "Submit the launchable task: [name:{} ,id:{} ] to launcher: [{}], retry_count: {}",
                        launchableExchangisTask.getName(), launchableExchangisTask.getId(), launcher.name(), retryCnt.get());
                boolean prepared = true;
                try {
                    Integer commitVersion = Optional.ofNullable(launchableExchangisTask.getCommitVersion()).orElse(0);
                    onEvent(new TaskPrepareEvent(launchableExchangisTask.getId() + "",
                            commitVersion, Calendar.getInstance().getTime()));
                    // Update the version
                    launchableExchangisTask.setCommitVersion(commitVersion + 1);
                } catch (Exception e){
                    LOG.warn("Fail to prepare submit: [name:{} id:{}], reason: [{}]",
                            launchableExchangisTask.getName(), launchableExchangisTask.getId(), e.getMessage(), e);
                    info(jobExecutionId, e.getMessage());
                    prepared = false;
                    if (rateApply) {
                        // Release the rateLimit, the params is not null
                        rateLimitService.releaseRateLimit(launchableExchangisTask.getRateParams(), launchableExchangisTask.getRateParamsMap());
                    }

                }
                if (prepared) {
                    try {
                        // Invoke launcher
                        Date launchTime = Calendar.getInstance().getTime();
                        launchedExchangisTask = launcher.launch(this.launchableExchangisTask);
                        //                launchedExchangisTask = new LaunchedExchangisTask(launchableExchangisTask);
                        launchedExchangisTask.setLaunchTime(launchTime);
                        info(jobExecutionId, "Success to submit task:[name:{}, id:{}] to Linkis [linkis_id: {}, info: {}]",
                                launchedExchangisTask.getName(), launchedExchangisTask.getId(), launchedExchangisTask.getLinkisJobId(), launchedExchangisTask.getLinkisJobInfo());
                    } catch (Exception e) {
                        info(jobExecutionId, "Launch task:[name:{} ,id:{}] fail, possible reason is: [{}]",
                                launchableExchangisTask.getName(), launchableExchangisTask.getId(), getActualCause(e).getMessage());
                        if (rateApply) {
                            // Release the rateLimit, the params is not null
                            rateLimitService.releaseRateLimit(launchableExchangisTask.getRateParams(), launchableExchangisTask.getRateParamsMap());
                        }
                        if (retryCnt.incrementAndGet() < getMaxRetryNum()) {
                            // Remove the launched task stored
                            //                    onEvent(new TaskDeleteEvent(String.valueOf(launchableExchangisTask.getId())));
                            throw new ExchangisSchedulerRetryException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() + "]", e);
                        }
                        throw new ExchangisSchedulerException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() + "]", e);
                    }
                }
            } catch (Exception e) {
                submitExp = e;
                if (e instanceof RateLimitNoLeftException) {
                    LOG.error(e.getMessage());
                    info(jobExecutionId,e.getMessage());
                    throw new ExchangisSchedulerException("Error occurred in applying for rateLimit resource: [" +  launchableExchangisTask.getId() + "]", e);
                }
                if (rateApply) {
                    // Release the rateLimit, the params is not null
                    rateLimitService.releaseRateLimit(launchableExchangisTask.getRateParams(), launchableExchangisTask.getRateParamsMap());
                }
                throw new ExchangisSchedulerException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() + "]", e);
            } finally {
                // Ignore the retry exception
                if (Objects.nonNull(submitCallback)&& !(submitExp instanceof ExchangisSchedulerRetryException)){
                    try {
                        submitCallback.accept(this, submitExp);
                    } catch (Exception e){
                        LOG.warn("Internal_Error: Fail to callback submit launch-able task: [ id: {}, name: {}, job_execution_id: {} ]"
                                , launchableExchangisTask.getId(), launchableExchangisTask.getName(), launchableExchangisTask.getJobExecutionId(), e);
                    }
                }
            }
            // Add the success/launched job into taskManager
            if (Objects.nonNull(this.taskManager) && Objects.nonNull(launchedExchangisTask)){
                boolean successAdd = true;
                try {
                    this.taskManager.addRunningTask(launchedExchangisTask);
                } catch (Exception e){
                    successAdd = false;
                    error(jobExecutionId, "Error occurred in adding running task: [id: {}, execution_id: {}] to taskManager," +
                                    " linkis_id: [{}], should kill the job in linkis!",
                            launchedExchangisTask.getId(), launchedExchangisTask.getJobExecutionId(), launchedExchangisTask.getLinkisJobId(), e);
                    LaunchedExchangisTask finalLaunchedExchangisTask = launchedExchangisTask;
                    Optional.ofNullable(launchedExchangisTask.getLauncherTask()).ifPresent(launcherTask -> {
                        try {
                            launcherTask.kill();
                        } catch (ExchangisTaskLaunchException ex){
                            LOG.error("Kill linkis_id: [{}] fail", finalLaunchedExchangisTask.getLinkisJobId(), e);
                        }
                    });
                }
                if (successAdd){
                    try {
                        onEvent(new TaskDequeueEvent(launchableExchangisTask.getId() + ""));
                    }catch (Exception e){
                        // Ignore the exception
                        LOG.warn("Fail to dequeue the launch-able task [{}]", launchableExchangisTask.getId(), e);
                    }
                    if (Objects.nonNull(this.loadBalancer)){
                        // Add the launchedExchangisTask to the load balance poller
                        List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> loadBalanceSchedulerTasks = this.loadBalancer.choose(launchedExchangisTask);
                        LaunchedExchangisTask finalLaunchedExchangisTask1 = launchedExchangisTask;
                        Optional.ofNullable(loadBalanceSchedulerTasks).ifPresent(tasks -> tasks.forEach(loadBalanceSchedulerTask -> {
                            loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().push(finalLaunchedExchangisTask1);
                        }));
                    }
                }
            }
        }
    }

    /**
     * Check if it can be submitted
     * @return boolean
     */
    public boolean isSubmitAble(){
        return submitAble.get();
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

    /**
     * Get actual cause
     * @param throwable throwable
     * @return Throwable
     */
    private Throwable getActualCause(Throwable throwable){
        Throwable t = throwable;
        while (Objects.nonNull(t.getCause())){
            t = t.getCause();
        }
        return t;
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
