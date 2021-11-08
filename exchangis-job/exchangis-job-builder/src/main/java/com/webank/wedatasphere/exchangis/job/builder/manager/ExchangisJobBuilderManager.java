package com.webank.wedatasphere.exchangis.job.builder.manager;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilderChain;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;
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
     <T extends ExchangisJobBase, E extends ExchangisJobBase>E doBuild(T originJob, Class<E> expectJobClass,
                                                                       ExchangisJobBuilderContext ctx) throws ExchangisJobException;

    /**
     *
     * @param jobBuilder job builder
     */
    <T extends ExchangisJobBase, E extends ExchangisJobBase>void addJobBuilder(ExchangisJobBuilder<T, E> jobBuilder);

    /**
     *
     * @param inputJob input job
     * @param outputJob output job
     * @param <T>
     * @param <E>
     * @return
     */
    <T extends ExchangisJobBase, E extends ExchangisJobBase>ExchangisJobBuilderChain<T, E> getJobBuilderChain(Class<T> inputJob, Class<E>  outputJob);

    /**
     * Reset builder chain
     * @param inputJob input job
     * @param outputJob output job
     * @param <T>
     * @param <E>
     */
    <T extends ExchangisJobBase, E extends ExchangisJobBase> void resetJobBuilder(Class<T> inputJob, Class<E> outputJob);

    /**
     * Invoke the 'initialize' method of chains
     */
    void initBuilderChains();
}
