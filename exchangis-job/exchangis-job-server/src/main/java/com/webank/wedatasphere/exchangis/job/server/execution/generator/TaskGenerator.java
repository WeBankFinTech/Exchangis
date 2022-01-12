package com.webank.wedatasphere.exchangis.job.server.execution.generator;

/**
 * To generate task for execution
 */

import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;

public interface TaskGenerator<T extends ExchangisJob> {
    /**
     * init method
     * @throws ExchangisJobException error in initializing
     */
    void init() throws ExchangisJobException;

    /**
     * Generate exchangis job
     * @param exchangisJob job extends ExchangisJob
     * @param execUser exec user
     * @return job has been handled
     * @throws ExchangisTaskGenerateException exception in generating
     */
    T generate(T exchangisJob, String execUser) throws ExchangisTaskGenerateException;

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

    /**
     * Add listeners
     * @param taskGenerateListener listener
     */
    void addListener(TaskGenerateListener taskGenerateListener);
}
