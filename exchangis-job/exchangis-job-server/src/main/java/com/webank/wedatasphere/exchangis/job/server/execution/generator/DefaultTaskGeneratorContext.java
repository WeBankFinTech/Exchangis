package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.ExchangisJobLogListener;
import org.springframework.stereotype.Component;

/**
 * Default generator context
 */
public class DefaultTaskGeneratorContext implements TaskGeneratorContext {


    @Override
    public ExchangisJobLogListener gtJobLogListener() {
        return null;
    }
}
