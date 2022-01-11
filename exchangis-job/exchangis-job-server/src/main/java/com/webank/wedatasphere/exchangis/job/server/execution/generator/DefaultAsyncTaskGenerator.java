package com.webank.wedatasphere.exchangis.job.server.execution.generator;


import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskGeneratorContext;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
// Scala doc need
import com.webank.wedatasphere.exchangis.job.server.execution.generator.AbstractTaskGenerator.GeneratorFunction;

import java.util.UUID;

/**
 * Async exec,
 * construct a JobGenerationSchedulerTask and them submit to TaskExecution
 */
@Component
public class DefaultAsyncTaskGenerator extends AbstractTaskGenerator{

//    @Resource
    private TaskExecution taskExecution;

    @Resource
    private ExchangisJobBuilderManager springJobBuilderManager;

    @Override
    protected void execute(GeneratorFunction generatorFunction,
                                             LaunchableExchangisJob launchableExchangisJob,
                                             TaskGeneratorContext ctx) throws ExchangisTaskGenerateException {
        // TODO Submit to taskExecution
    }

    @Override
    public ExchangisJobBuilderManager getExchangisJobBuilderManager() {
        return springJobBuilderManager;
    }

    public static void main(String[] args){
        System.out.println(UUID.randomUUID());
    }
}
