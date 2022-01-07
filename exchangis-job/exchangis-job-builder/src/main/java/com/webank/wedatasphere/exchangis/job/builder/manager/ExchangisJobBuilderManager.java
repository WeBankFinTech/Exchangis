package com.webank.wedatasphere.exchangis.job.builder.manager;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilderChain;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Define the builder manager interface
 */
public interface ExchangisJobBuilderManager {

    /**
     *
     * @param originJob origin job
     * @param expectJobClass expect job class
     * @param <T> input class
     * @param <E> output class
     * @return output
     */
     <T extends ExchangisJob, E extends ExchangisJob>E doBuild(T originJob, Class<E> expectJobClass,
                                                               ExchangisJobBuilderContext ctx) throws ExchangisJobException;

    <T extends ExchangisJob, E extends ExchangisJob>E doBuild(T originJob, Class<T> inputJobClass, Class<E> expectJobClass,
                                                              ExchangisJobBuilderContext ctx) throws ExchangisJobException;
    /**
     *
     * @param jobBuilder job builder
     */
    <T extends ExchangisJob, E extends ExchangisJob>void addJobBuilder(ExchangisJobBuilder<T, E> jobBuilder);

    /**
     *
     * @param inputJob input job
     * @param outputJob output job
     * @param <T>
     * @param <E>
     * @return
     */
    <T extends ExchangisJob, E extends ExchangisJob>ExchangisJobBuilderChain<T, E> getJobBuilderChain(Class<T> inputJob, Class<E>  outputJob);

    /**
     * Reset builder chain
     * @param inputJob input job
     * @param outputJob output job
     * @param <T>
     * @param <E>
     */
    <T extends ExchangisJob, E extends ExchangisJob> void resetJobBuilder(Class<T> inputJob, Class<E> outputJob);

    /**
     * Invoke the 'initialize' method of chains
     */
    void initBuilderChains();
}
