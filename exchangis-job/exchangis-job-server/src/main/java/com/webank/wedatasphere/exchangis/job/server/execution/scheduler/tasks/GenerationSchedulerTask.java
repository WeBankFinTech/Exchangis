package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGeneratorContext;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.AbstractTaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import org.apache.linkis.scheduler.queue.JobInfo;

public class GenerationSchedulerTask extends AbstractExchangisSchedulerTask {

    private LaunchableExchangisJob launchableExchangisJob;

    private TaskGeneratorContext ctx;

    private TaskGenerator<LaunchableExchangisJob> taskGenerator;
    /**
     * Each schedule task should has an id
     *
     */
    public GenerationSchedulerTask(TaskGenerator<LaunchableExchangisJob> taskGenerator,
                                   ExchangisJobInfo exchangisJobInfo) throws ExchangisTaskGenerateException {
        super("");
        this.taskGenerator = taskGenerator;
        this.launchableExchangisJob = taskGenerator.init(exchangisJobInfo);
        this.ctx = taskGenerator.getTaskGeneratorContext();
        this.scheduleId = this.launchableExchangisJob.getJobExecutionId();
    }

    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        try {
            this.taskGenerator.generate(this.launchableExchangisJob, this.getTenancy());
        } catch (Exception e) {
            String errorMessage = "Exception in generating launchable tasks: [ name: " + launchableExchangisJob.getExchangisJobInfo().getName() +
                    ", job_execution_id: " + launchableExchangisJob.getJobExecutionId() + "]";
            // TODO retry the generating progress
            if (!(e instanceof ExchangisTaskGenerateException)){
                // Retry the generate progress
//                throw new ExchangisSchedulerRetryException(errorMessage, e);
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
