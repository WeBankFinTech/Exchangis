package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;
import org.springframework.stereotype.Component;

/**
 * Default generator context
 */
@Component
public class DefaultTaskGeneratorContext implements TaskGeneratorContext {


    @Override
    public ExchangisJobLogListener getOrCreateJobLogListener() {
        return null;
    }
}
