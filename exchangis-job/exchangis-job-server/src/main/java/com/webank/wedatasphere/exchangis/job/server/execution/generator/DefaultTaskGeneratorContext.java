package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;
import org.springframework.stereotype.Component;

/**
 * Default generator context
 */
public class DefaultTaskGeneratorContext implements TaskGeneratorContext {

    private ExchangisJobLogListener jobLogListener;

    public DefaultTaskGeneratorContext(ExchangisJobLogListener jobLogListener){
        this.jobLogListener = jobLogListener;
    }

    @Override
    public ExchangisJobLogListener gtJobLogListener() {
        return this.jobLogListener;
    }
}
