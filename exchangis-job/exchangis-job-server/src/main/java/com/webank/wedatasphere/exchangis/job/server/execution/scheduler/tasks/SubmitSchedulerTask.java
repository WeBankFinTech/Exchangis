package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Enumeration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Submit scheduler task
 */
public class SubmitSchedulerTask extends AbstractExchangisSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(SubmitSchedulerTask.class);

    private LaunchableExchangisTask launchableExchangisTask;

    private TaskManager<LaunchedExchangisTask> taskManager;

    private ExchangisTaskLauncher<LaunchableExchangisTask> launcher;

    private TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer;

    private Callable<Boolean> submitCondition;

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
        try {
            submitAble = submitCondition.call();
        } catch (Exception e){
            throw new ExchangisSchedulerRetryException("Error occurred in examining submit condition for task: [" + launchableExchangisTask.getId() + "]", e);
        }
        if (submitAble) {
            LOG.info("Submit the launchable task: [" + launchableExchangisTask.getId() + "] to launcher: [" + launcher.name() + "]");
            LaunchedExchangisTask launchedExchangisTask;
            try {
                // Invoke launcher
                launchedExchangisTask = launcher.launch(this.launchableExchangisTask);
            } catch (Exception e) {
                throw new ExchangisSchedulerException("Error occurred in invoking launching method for task: [" + launchableExchangisTask.getId() +"]", e);
            }
            // Add the success/launched job into taskManager
            if (Objects.nonNull(this.taskManager)){
                boolean successAdd = true;
                try {
                    this.taskManager.addRunningTask(launchedExchangisTask);
                } catch (Exception e){
                    successAdd = false;
                    LOG.error("Error occurred in adding running task: [{}] to taskManager, linkis_id: [{}], should kill the job in linkis!",
                            launchedExchangisTask.getId(), launchedExchangisTask.getLinkisJobId(), e);
                    launchedExchangisTask.kill();
                }
                if (successAdd && Objects.nonNull(this.loadBalancer)) {
                    // Add the launchedExchangisTask to the load balance poller
                    List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> loadBalanceSchedulerTasks = this.loadBalancer.choose(launchedExchangisTask);
                    Optional.ofNullable(loadBalanceSchedulerTasks).ifPresent(tasks -> tasks.forEach(loadBalanceSchedulerTask -> {
                        loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().push(launchedExchangisTask);
                    }));
                }
            }
        }
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

    public ExchangisTaskLauncher<LaunchableExchangisTask> getLauncher() {
        return launcher;
    }

    public void setLauncher(ExchangisTaskLauncher<LaunchableExchangisTask> launcher) {
        this.launcher = launcher;
    }

    public TaskSchedulerLoadBalancer<LaunchedExchangisTask> getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(TaskSchedulerLoadBalancer<LaunchedExchangisTask> loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
}
