package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.ServiceInExchangisJobBuilderContext;

/**
 * Abstract implement for engine job builder
 */
public abstract class AbstractLoggingExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisBase> extends
        AbstractExchangisJobBuilder<T, E> {

    /**
     * Get builder context
     * @return context
     * @throws ExchangisJobException.Runtime exception
     */
    protected static ServiceInExchangisJobBuilderContext getServiceInBuilderContext() throws ExchangisJobException.Runtime{
        ExchangisJobBuilderContext context = getCurrentBuilderContext();
        if (!(context instanceof ServiceInExchangisJobBuilderContext)) {
            throw new ExchangisJobException.Runtime(-1, "The job builder context cannot not be casted to " + ServiceInExchangisJobBuilderContext.class.getCanonicalName(), null);
        }
        return (ServiceInExchangisJobBuilderContext)context;
    }

    /**
     * Warn message
     * @param message message
     */
    public static void warn(String message, Object... args){
        getServiceInBuilderContext().getLogging().warn(null, message, args);
    }

    public static void warn(String message, Throwable t){
        getServiceInBuilderContext().getLogging().warn(null, message, t);
    }

    /**
     * Info message
     * @param message message
     */
    public static void info(String message, Object... args){
        getServiceInBuilderContext().getLogging().info(null, message, args);
    }

    public static void info(String message, Throwable t){
        getServiceInBuilderContext().getLogging().info(null, message, t);
    }
}
