package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implement for engine job builder
 */
public abstract class AbstractLoggingExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisBase> extends
        AbstractExchangisJobBuilder<T, E> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoggingExchangisJobBuilder.class);
    /**
     * Get builder context
     * @return context
     * @throws ExchangisJobException.Runtime exception
     */
    protected static SpringExchangisJobBuilderContext getSpringBuilderContext() throws ExchangisJobException.Runtime{
        ExchangisJobBuilderContext context = getCurrentBuilderContext();
        if (!(context instanceof SpringExchangisJobBuilderContext)) {
            throw new ExchangisJobException.Runtime(-1, "The job builder context cannot not be casted to " + SpringExchangisJobBuilderContext.class.getCanonicalName(), null);
        }
        return (SpringExchangisJobBuilderContext)context;
    }

    /**
     * Warn message
     * @param message message
     */
    public static void warn(String message, Object... args){
        getSpringBuilderContext().getLogging().warn(null, message, args);
    }

    public static void warn(String message, Throwable t){
        getSpringBuilderContext().getLogging().warn(null, message, t);
    }

    /**
     * Info message
     * @param message message
     */
    public static void info(String message, Object... args){
        getSpringBuilderContext().getLogging().info(null, message, args);
    }

    public static void info(String message, Throwable t){
        getSpringBuilderContext().getLogging().info(null, message, t);
    }

    /**
     * Get bean in spring context
     * @param beanClass bean class
     * @param <T>
     * @return
     */
    public static <T>T getBean(Class<T> beanClass){
        return getSpringBuilderContext().getBean(beanClass);
    }
}
