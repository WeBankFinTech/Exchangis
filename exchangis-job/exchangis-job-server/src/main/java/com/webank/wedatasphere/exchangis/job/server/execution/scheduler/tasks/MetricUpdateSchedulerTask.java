package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

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
        LOG.info("Metrics update task: [" + launchedExchangisTask.getId() + "]");
//        launchedExchangisTask.callMetricsUpdate();
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
        return getId();
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }
}
