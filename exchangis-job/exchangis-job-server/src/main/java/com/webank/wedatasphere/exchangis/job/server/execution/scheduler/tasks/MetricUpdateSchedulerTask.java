package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;

/**
 * Metric update scheduler task
 */
public class MetricUpdateSchedulerTask extends ExchangisSchedulerTask {
    /**
     * Each schedule task should has an id
     *
     * @param scheduleId schedule id
     */
    public MetricUpdateSchedulerTask(String scheduleId) {
        super(scheduleId);
    }

    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }
}
