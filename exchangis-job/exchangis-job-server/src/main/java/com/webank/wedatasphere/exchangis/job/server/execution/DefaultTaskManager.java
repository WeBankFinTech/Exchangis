package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;

/**
 * Default implement
 */
public class DefaultTaskManager extends AbstractTaskManager{

    /**
     * Log listener
     */
    private JobLogListener jobLogListener;

    public DefaultTaskManager(JobLogListener jobLogListener) {
        this.jobLogListener = jobLogListener;
    }

    @Override
    public JobLogListener getJobLogListener() {
        return  this.jobLogListener;
    }
}
