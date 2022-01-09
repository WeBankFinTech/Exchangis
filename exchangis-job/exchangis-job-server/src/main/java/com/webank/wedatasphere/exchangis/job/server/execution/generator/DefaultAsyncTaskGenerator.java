package com.webank.wedatasphere.exchangis.job.server.execution.generator;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskGeneratorContext;

/**
 * Async exec
 */
public class DefaultAsyncTaskGenerator extends AbstractTaskGenerator{
    @Override
    protected LaunchableExchangisJob execute(GeneratorFunction generatorFunction, TaskGeneratorContext ctx) throws ExchangisTaskGenerateException {
        return null;
    }
}
