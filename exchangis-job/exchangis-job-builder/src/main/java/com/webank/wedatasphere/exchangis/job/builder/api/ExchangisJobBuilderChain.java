package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

/**
 * Builder chain
 */
public interface ExchangisJobBuilderChain<T extends ExchangisJobBase, E extends ExchangisJobBase> {

    /**
     * Register builder
     * @param jobBuilder builder
     * @return boolean
     */
    boolean registerBuilder(ExchangisJobBuilder<T, E> jobBuilder);

    /**
     * Build method
     * @param inputJob input job
     * @param ctx context
     * @return output job
     */
    E build(T inputJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException;

    /**
     * Init method
     */
    void initialize();

    /**
     * Clean method
     */
    void clean();
}
