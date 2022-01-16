package com.webank.wedatasphere.exchangis.job.server.execution;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;

/**
 * Default implement
 */
public class DefaultTaskManager extends AbstractTaskManager{
    public DefaultTaskManager(TaskExecutionListener listener) {
        super(listener);
    }

    @Override
    public ExchangisJobLogListener getJobLogListener() {
        return null;
    }
}
