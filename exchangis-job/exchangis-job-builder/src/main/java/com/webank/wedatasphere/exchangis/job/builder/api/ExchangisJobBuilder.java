package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Builder interface
 * @param <T> input job
 * @param <E> output job
 */
public interface ExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisBase> {

    /**
     * Input job class
     * @return class type
     */
    Class<T> inputJob();

    /**
     * Output job class
     * @return class type
     */
    Class<E> outputJob();

    /**
     * Priority
     * @return value
     */
    int priority();

    /**
     * If the input job can be built
     * @param inputJob input job entity
     * @return boolean
     */
    boolean canBuild(T inputJob);

    /**
     * Main entrance of building
     * @param inputJob input job
     * @param expectOut expect output entity (can be null or get from output of early builder)
     * @param ctx context
     * @return outputJob
     */
    E build(T inputJob, E expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException;
}
