package com.webank.wedatasphere.exchangis.job.server.execution.generator;


import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateErrorEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateSuccessEvent;
import org.apache.linkis.common.exception.ErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Async exec,
 * construct a JobGenerationSchedulerTask and them submit to TaskExecution
 */
public class DefaultTaskGenerator extends AbstractTaskGenerator{


    protected TaskGeneratorContext ctx;

    private ExchangisJobBuilderManager jobBuilderManager;

    public DefaultTaskGenerator(TaskGeneratorContext ctx, ExchangisJobBuilderManager jobBuilderManager){
        this.ctx = ctx;
        this.jobBuilderManager = jobBuilderManager;
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
    protected void execute(LaunchableExchangisJob launchableExchangisJob,
                                             TaskGeneratorContext generatorContext, String tenancy) throws ErrorException {
        ExchangisTaskGenerateException throwable;
        ExchangisJobInfo jobInfo = launchableExchangisJob.getExchangisJobInfo();
        List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>();
        if (Objects.isNull(jobInfo)){
            throwable = new ExchangisTaskGenerateException("Job information is empty in launchable exchangis job", null);
            onEvent(new TaskGenerateErrorEvent(launchableExchangisJob, throwable));
            throw throwable;
        }
        ExchangisJobBuilderManager jobBuilderManager = getExchangisJobBuilderManager();
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext(jobInfo, generatorContext.getJobLogListener());
        ctx.putEnv("USER_NAME", tenancy);
        // ExchangisJobInfo -> TransformExchangisJob(SubExchangisJob)
        try {
            TransformExchangisJob transformJob = jobBuilderManager.doBuild(jobInfo, TransformExchangisJob.class, ctx);
            List<ExchangisEngineJob> engineJobs = new ArrayList<>();
            for (SubExchangisJob subExchangisJob : transformJob.getSubJobSet()){
                // Will deal with the parameters in source/sink of job
                Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
                        SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
            }
            // List<ExchangisEngineJob> -> List<LaunchableExchangisTask>
            for (ExchangisEngineJob engineJob : engineJobs){
                Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
                        ExchangisEngineJob.class, LaunchableExchangisTask.class, ctx)).ifPresent(launchableExchangisTasks :: add);
            }
            if (launchableExchangisTasks.isEmpty()){
                throw new ExchangisTaskGenerateException("The result set of launchable tasks is empty, please examine your launchable job entity," +
                        " content: [" + jobInfo.getJobContent() + "]", null);
            }
            launchableExchangisJob.setLaunchableExchangisTasks(launchableExchangisTasks);
            onEvent(new TaskGenerateSuccessEvent(launchableExchangisJob));
        } catch (Exception e) {
            if (e instanceof ExchangisTaskGenerateException){
                // Just throws the generate exception
                throwable =  (ExchangisTaskGenerateException)e;
            } else {
                throwable = new ExchangisTaskGenerateException("Error in generating launchable tasks", e);
            }
            onEvent(new TaskGenerateErrorEvent(launchableExchangisJob, throwable));
            throw throwable;
        }
    }

    @Override
    public ExchangisJobBuilderManager getExchangisJobBuilderManager() {
        return jobBuilderManager;
    }

}
