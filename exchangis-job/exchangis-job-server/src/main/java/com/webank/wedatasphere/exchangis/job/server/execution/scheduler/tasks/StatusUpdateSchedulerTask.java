package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.DelayLoadBalancePoller;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Status update scheduler task
 */
public class StatusUpdateSchedulerTask extends AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(StatusUpdateSchedulerTask.class);
    private static final CommonVars<Long> STATUS_UPDATE_INTERVAL = CommonVars.apply("wds.exchangis.job.scheduler.task.status.update.interval-in-millis", 5000L);

    private TaskManager<LaunchedExchangisTask> taskManager;

    public StatusUpdateSchedulerTask(TaskManager<LaunchedExchangisTask> taskManager){
        this.taskManager = taskManager;
    }
    @Override
    protected void onPoll(LaunchedExchangisTask launchedExchangisTask) throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        LOG.info("Status update task: [{}] in scheduler: [{}]", launchedExchangisTask.getId(), getName());
        AccessibleLauncherTask launcherTask = launchedExchangisTask.getLauncherTask();
        try{
            TaskProgressInfo progressInfo = launcherTask.getProgressInfo();
            if (Objects.nonNull(progressInfo)){
                this.taskManager.refreshRunningTaskProgress(launchedExchangisTask, progressInfo);
            }
            this.taskManager.refreshRunningTaskStatus(launchedExchangisTask, launcherTask.getLocalStatus());
        } catch (ExchangisTaskLaunchException e){
            throw new ExchangisSchedulerException("Fail to update status(progress) for task: [" + launchedExchangisTask.getTaskId() + "]", e);
        }
    }

    @Override
    protected LoadBalancePoller<LaunchedExchangisTask> createLoadBalancePoller() {
        return new DelayLoadBalancePoller<LaunchedExchangisTask>() {
            @Override
            protected long getDelayTimeInMillis(LaunchedExchangisTask element) {
                return System.currentTimeMillis() + STATUS_UPDATE_INTERVAL.getValue();
            }
        };
    }

    @Override
    public String getName() {
        return getId() + "-Status";
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }
}
