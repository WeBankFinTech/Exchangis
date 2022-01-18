package com.webank.wedatasphere.exchangis.job.server.execution.generator;


import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.scheduler.exception.SchedulerErrorException;

// Scala doc need


/**
 * Async exec,
 * construct a JobGenerationSchedulerTask and them submit to TaskExecution
 */
public class DefaultTaskGenerator extends AbstractTaskGenerator{

    private TaskExecution<LaunchableExchangisTask> taskExecution;

    protected TaskGeneratorContext ctx;

    private ExchangisJobBuilderManager jobBuilderManager;

    public DefaultTaskGenerator(TaskGeneratorContext ctx, ExchangisJobBuilderManager jobBuilderManager,
                                TaskExecution<LaunchableExchangisTask> taskExecution){
        this.ctx = ctx;
        this.jobBuilderManager = jobBuilderManager;
        this.taskExecution = taskExecution;
    }

    @Override
    public TaskGeneratorContext getTaskGeneratorContext() {
        return ctx;
    }

    @Override
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
        return jobBuilderManager;
    }

}
