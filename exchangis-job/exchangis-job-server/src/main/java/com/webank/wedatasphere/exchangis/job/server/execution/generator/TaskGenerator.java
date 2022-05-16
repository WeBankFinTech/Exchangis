package com.webank.wedatasphere.exchangis.job.server.execution.generator;

/**
 * To generate task for execution
 */

import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.log.JobServerLogging;

public interface TaskGenerator<T extends ExchangisJob> extends JobServerLogging<T> {
    /**
     * init method
     * @throws ExchangisJobException error in initializing
     */
    void init() throws ExchangisJobException;

    /**
     * Init the job info to suitable input
     * @param jobInfo
     * @return
     */
    T init(ExchangisJobInfo jobInfo) throws ExchangisTaskGenerateException;
    /**
     * Generate exchangis job (has tasks)
     * @param exchangisJob job extends ExchangisJob
     * @param tenancy act as exec user
     * @return job has been handled
     * @throws ExchangisTaskGenerateException exception in generating
     */
    T generate(T exchangisJob, String tenancy) throws ExchangisTaskGenerateException;

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
