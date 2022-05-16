package com.webank.wedatasphere.exchangis.job.builder.manager;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilderChain;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisBase;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Define the builder manager interface
 */
public interface ExchangisJobBuilderManager {

    /**
     *
     * @param originJob origin job
     * @param expectEntityClass expect entity class
     * @param <T> input class
     * @param <E> output class
     * @return output
     */
     <T extends ExchangisJob, E extends ExchangisBase>E doBuild(T originJob, Class<E> expectEntityClass,
                                                                ExchangisJobBuilderContext ctx) throws ExchangisJobException;

    <T extends ExchangisJob, E extends ExchangisBase>E doBuild(T originJob, Class<T> inputJobClass, Class<E> expectEntityClass,
                                                              ExchangisJobBuilderContext ctx) throws ExchangisJobException;
    /**
     *
     * @param jobBuilder job builder
     */
    <T extends ExchangisJob, E extends ExchangisBase>void addJobBuilder(ExchangisJobBuilder<T, E> jobBuilder);

    /**
     *
     * @param inputJob input job
     * @param outputEntity output entity
     * @param <T>
     * @param <E>
     * @return
     */
    <T extends ExchangisJob, E extends ExchangisBase>ExchangisJobBuilderChain<T, E> getJobBuilderChain(Class<T> inputJob, Class<E>  outputEntity);

    /**
     * Reset builder chain
     * @param inputJob input job
     * @param outputEntity output entity
     * @param <T>
     * @param <E>
     */
    <T extends ExchangisJob, E extends ExchangisBase> void resetJobBuilder(Class<T> inputJob, Class<E> outputEntity);

    /**
     * Invoke the 'initialize' method of chains
     */
    void initBuilderChains();
}
