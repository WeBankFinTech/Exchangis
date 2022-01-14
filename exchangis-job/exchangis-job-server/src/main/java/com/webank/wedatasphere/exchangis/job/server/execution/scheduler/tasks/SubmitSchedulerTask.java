package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;

import java.util.concurrent.Callable;

/**
 * Submit scheduler task
 */
public class SubmitSchedulerTask extends ExchangisSchedulerTask {
    private LaunchableExchangisTask launchableExchangisTask;

    private TaskManager<LaunchedExchangisTask> taskManager;

    private ExchangisTaskLauncher<? extends LaunchableExchangisTask> jobLauncher;

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
        Boolean submitAble = false;
        try {
            submitAble = submitCondition.call();
        } catch (Exception e){

        }
        // TODO invoke launcher
        // TODO add the success/launched job into taskManager

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

    public ExchangisTaskLauncher<? extends LaunchableExchangisTask> getJobLauncher() {
        return jobLauncher;
    }

    public void setJobLauncher(ExchangisTaskLauncher<? extends LaunchableExchangisTask> jobLauncher) {
        this.jobLauncher = jobLauncher;
    }
}
