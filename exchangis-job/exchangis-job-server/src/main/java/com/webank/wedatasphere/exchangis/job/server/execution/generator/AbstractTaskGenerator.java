package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.manager.DefaultExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskGeneratorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains the main progress for generating
 */
public abstract class AbstractTaskGenerator implements TaskGenerator<LaunchableExchangisJob> {

    private GeneratorFunction generatorFunction;

    @Override
    public void init() throws ExchangisJobErrorException {
        this.generatorFunction = generatorFunction();
    }

    @Override
    public LaunchableExchangisJob generate(LaunchableExchangisJob exchangisJob) throws ExchangisTaskGenerateException {
        if (Objects.isNull(generatorFunction)){
            this.generatorFunction = generatorFunction();
        }
        if (Objects.isNull(this.generatorFunction)){
            throw new ExchangisTaskGenerateException("Generator function is emtpy, please define it before generating!", null);
        }
        // TODO persist the launchable exchangis before
        return execute(this.generatorFunction, getTaskGeneratorContext());
    }


    @Override
    public TaskGeneratorContext getTaskGeneratorContext() {
        return new DefaultTaskGeneratorContext();
    }


    /**
     * Use the default job builder manager
     * @return default manager
     */
    @Override
    public ExchangisJobBuilderManager getExchangisJobBuilderManager() {
        return new DefaultExchangisJobBuilderManager();
    }

    protected GeneratorFunction generatorFunction(){
        return launchableExchangisJob -> {
            ExchangisJobInfo jobInfo = launchableExchangisJob.getExchangisJobInfo();
            if (Objects.isNull(jobInfo)){
                throw new ExchangisTaskGenerateException("Job information is empty in launchable exchangis job", null);
            }
            ExchangisJobBuilderManager jobBuilderManager = getExchangisJobBuilderManager();
            ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
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
                List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>();
                for (ExchangisEngineJob engineJob : engineJobs){
                    Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
                            ExchangisEngineJob.class, LaunchableExchangisTask.class, ctx)).ifPresent(launchableExchangisTasks :: add);
                }
                if (launchableExchangisTasks.isEmpty()){
                    throw new ExchangisTaskGenerateException("The result set of launchable tasks is empty, please examine your launchable job entity," +
                            " content: [" + jobInfo.getJobContent() + "]", null);
                }
                launchableExchangisJob.setLaunchableExchangisTasks(launchableExchangisTasks);
            } catch (ExchangisJobException e) {
                if (e instanceof ExchangisTaskGenerateException){
                    // Just throws the generate exception
                    throw (ExchangisTaskGenerateException)e;
                }
                throw new ExchangisTaskGenerateException("Error in building launchable tasks", e);
            }
            return launchableExchangisJob;
        };
    }
    /**
     * Execute method
     * @param generatorFunction generator function
     * @param ctx context
     */
    protected abstract LaunchableExchangisJob execute(GeneratorFunction generatorFunction, TaskGeneratorContext ctx) throws ExchangisTaskGenerateException;

    @FunctionalInterface
    interface GeneratorFunction{
        /**
         * Apply function
         * @param launchableExchangisJob origin job
         */
        LaunchableExchangisJob apply(LaunchableExchangisJob launchableExchangisJob) throws ExchangisTaskGenerateException;
    }
}
