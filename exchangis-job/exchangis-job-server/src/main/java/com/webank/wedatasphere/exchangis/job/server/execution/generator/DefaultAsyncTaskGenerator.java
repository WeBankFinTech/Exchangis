package com.webank.wedatasphere.exchangis.job.server.execution.generator;


import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.scheduler.exception.SchedulerErrorException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
// Scala doc need

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
    protected TaskGeneratorContext ctx;

    @Resource
    private ExchangisJobBuilderManager springJobBuilderManager;

    @Override
    public TaskGeneratorContext getTaskGeneratorContext() {
        return ctx;
    }

    @Override
    @PostConstruct
    public void init() throws ExchangisJobException {
        super.init();

    }

    @Override
    protected void execute(GeneratorFunction generatorFunction,
                                             LaunchableExchangisJob launchableExchangisJob,
                                             TaskGeneratorContext ctx, String execUser) throws ErrorException {
        try {
            GenerationSchedulerTask schedulerTask = new GenerationSchedulerTask(launchableExchangisJob, generatorFunction, ctx);
            // Set the tenancy as execUser
            schedulerTask.setTenancy(execUser);
            // Submit the scheduler task
            taskExecution.submit(schedulerTask);
        } catch (Exception e){
            String errorMessage = "Fail to async submit launchable job: [ name: " + launchableExchangisJob.getExchangisJobInfo().getName() +
                    ", job_execution_id: " + launchableExchangisJob.getJobExecutionId() + "]";
            if (e instanceof SchedulerErrorException) {
                throw new ExchangisSchedulerException(errorMessage, e);
            }
            throw new ExchangisTaskGenerateException(errorMessage, e);
        }
    }

    @Override
    public ExchangisJobBuilderManager getExchangisJobBuilderManager() {
        return springJobBuilderManager;
    }

}