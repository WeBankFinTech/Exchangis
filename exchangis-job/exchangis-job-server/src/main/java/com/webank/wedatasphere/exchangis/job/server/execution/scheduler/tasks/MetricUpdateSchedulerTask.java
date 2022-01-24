package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.DelayLoadBalancePoller;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Metric update scheduler task
 */
public class MetricUpdateSchedulerTask extends AbstractLoadBalanceSchedulerTask<LaunchedExchangisTask> {

    private static final Logger LOG = LoggerFactory.getLogger(MetricUpdateSchedulerTask.class);

    private static final CommonVars<Long> METRIC_UPDATE_INTERVAL = CommonVars.apply("wds.exchangis.job.scheduler.task.metric.update.interval-in-millis", 3000L);

    private TaskManager<LaunchedExchangisTask> taskManager;

    public MetricUpdateSchedulerTask(TaskManager<LaunchedExchangisTask> taskManager){
        this.taskManager = taskManager;
    }

    @Override
    protected void onPoll(LaunchedExchangisTask launchedExchangisTask) throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        LOG.trace("Metrics update task: [{}] in scheduler: [{}]", launchedExchangisTask.getTaskId(), getName());
        AccessibleLauncherTask launcherTask = launchedExchangisTask.getLauncherTask();
        try {
            Map<String, Object> metricsInfo = launcherTask.getMetricsInfo();
            if (Objects.nonNull(metricsInfo)){
                taskManager.refreshRunningTaskMetrics(launchedExchangisTask, metricsInfo);
            }
        } catch (ExchangisTaskLaunchException e) {
            throw new ExchangisSchedulerException("Fail to get metrics information for task: [" + launchedExchangisTask.getTaskId() + "]", e);
        }
    }

    @Override
    protected LoadBalancePoller<LaunchedExchangisTask> createLoadBalancePoller() {
        return new DelayLoadBalancePoller<LaunchedExchangisTask>() {
            @Override
            protected long getDelayTimeInMillis(LaunchedExchangisTask element) {
                return System.currentTimeMillis() + METRIC_UPDATE_INTERVAL.getValue();
            }
        };
    }

    @Override
    public String getName() {
        return getId() + "-Metric";
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }
}
