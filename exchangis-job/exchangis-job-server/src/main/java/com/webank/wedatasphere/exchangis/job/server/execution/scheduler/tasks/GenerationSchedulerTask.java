package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGeneratorContext;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.AbstractTaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;

public class GenerationSchedulerTask extends AbstractExchangisSchedulerTask {

    private AbstractTaskGenerator.GeneratorFunction generatorFunction;

    private LaunchableExchangisJob launchableExchangisJob;

    private TaskGeneratorContext ctx;
    /**
     * Each schedule task should has an id
     *
     */
    public GenerationSchedulerTask(LaunchableExchangisJob launchableExchangisJob,
                                   AbstractTaskGenerator.GeneratorFunction generatorFunction,
                                   TaskGeneratorContext ctx) {
        super(launchableExchangisJob.getJobExecutionId());
        this.generatorFunction = generatorFunction;
        this.launchableExchangisJob = launchableExchangisJob;
        this.ctx = ctx;
    }

    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        try {
            generatorFunction.apply(launchableExchangisJob, ctx, getTenancy());
        } catch (Exception e) {
            String errorMessage = "Exception in generating launchable job: [ name: " + launchableExchangisJob.getExchangisJobInfo().getName() +
                    ", job_execution_id: " + launchableExchangisJob.getJobExecutionId() + "]";
            if (!(e instanceof ExchangisTaskGenerateException)){
                // Retry the generate progress
                throw new ExchangisSchedulerRetryException(errorMessage, e);
            }
            throw new ExchangisSchedulerException(errorMessage, e);
        }
    }

    @Override
    public String getName() {
        return "Scheduler-GenerationTask-" + getId();
    }

    @Override
    public JobInfo getJobInfo() {
        return null;
    }
}
