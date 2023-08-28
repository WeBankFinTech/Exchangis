package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
import com.webank.wedatasphere.exchangis.job.server.log.JobServerLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/**
 * Service in job builder context
 */
public class SpringExchangisJobBuilderContext extends ExchangisJobBuilderContext {


    /**
     * Job execution id
     */
    private String jobExecutionId;

    /**
     * Logging
     */
    private JobServerLogging<ExchangisJobBuilder<?, ?>> logging;

    private ApplicationContext applicationContext;

    public SpringExchangisJobBuilderContext(ExchangisJobInfo originalJob,
                                            ApplicationContext applicationContext, JobLogListener jobLogListener) {
        super(originalJob);
        this.applicationContext = applicationContext;
        this.logging = new JobServerLogging<ExchangisJobBuilder<?, ?>>() {
            @Override
            public Logger getLogger() {
                return Objects.nonNull(currentBuilder)?
                        LoggerFactory.getLogger(currentBuilder.getClass()) : null;
            }

            @Override
            public JobLogListener getJobLogListener() {
                return jobLogListener;
            }

            @Override
            public JobLogEvent getJobLogEvent(JobLogEvent.Level level, ExchangisJobBuilder<?, ?>  builder, String message, Object... args) {
                return new JobLogEvent(level, originalJob.getExecuteUser(), jobExecutionId, message, args);
            }
        };
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public <T>T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public JobServerLogging<ExchangisJobBuilder<?, ?>> getLogging() {
        return logging;
    }
}
