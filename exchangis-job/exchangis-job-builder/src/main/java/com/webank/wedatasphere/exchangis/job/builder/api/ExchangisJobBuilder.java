package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Builder interface
 * @param <T> input job
 * @param <E> output job
 */
public interface ExchangisJobBuilder<T extends ExchangisJobBase, E extends ExchangisJobBase> {

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
     * @param expectJob expect job (can be null or get from output of early builder)
     * @param ctx context
     * @return outputJob
     */
    E buildJob(T inputJob, E expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException;
}
