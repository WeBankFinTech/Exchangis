package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;

/**
 * Generator context
 */
public interface TaskGeneratorContext {

    /**
     * Job Log listener
     * @return
     */
    ExchangisJobLogListener gtJobLogListener();
}
