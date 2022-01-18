package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;

/**
 * Default implement
 */
public class DefaultTaskManager extends AbstractTaskManager{

    /**
     * Log listener
     */
    private ExchangisJobLogListener jobLogListener;

    public DefaultTaskManager(ExchangisJobLogListener jobLogListener) {
        this.jobLogListener = jobLogListener;
    }

    @Override
    public ExchangisJobLogListener getJobLogListener() {
        return  this.jobLogListener;
    }
}
