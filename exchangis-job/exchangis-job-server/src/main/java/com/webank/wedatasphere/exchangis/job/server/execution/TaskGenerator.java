package com.webank.wedatasphere.exchangis.job.server.execution;

/**
 * To generate task for execution
 */

import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;

public interface TaskGenerator<T extends ExchangisJob> {
    /**
     * init method
     * @throws ExchangisJobErrorException error in initializing
     */
    void init() throws ExchangisJobErrorException;

    /**
     * Generate exchangis job
     * @param exchangisJob job extends ExchangisJob
     * @return job has been handled
     * @throws ExchangisTaskGenerateException exception in generating
     */
    T generate(T exchangisJob) throws ExchangisTaskGenerateException;

    /**
     * Get generator context
     * @return context
     */
    TaskGeneratorContext getTaskGeneratorContext();

    /**
     * Get job builder manager
     * @return
     */
    ExchangisJobBuilderManager getExchangisJobBuilderManager();
}
