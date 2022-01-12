package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.manager.DefaultExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateErrorEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateInitEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateSuccessEvent;
import org.apache.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Contains the main progress for generating
 */
public abstract class AbstractTaskGenerator implements TaskGenerator<LaunchableExchangisJob> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskGenerator.class);

    private GeneratorFunction generatorFunction;

    private List<TaskGenerateListener> listeners = new ArrayList<>();

    protected TaskGeneratorContext generatorContext;

    @Override
    public void init() throws ExchangisJobException {
        this.generatorFunction = generatorFunction();
    }

    @Override
    public LaunchableExchangisJob generate(LaunchableExchangisJob launchableExchangisJob, String execUser) throws ExchangisTaskGenerateException {
        if (Objects.isNull(generatorFunction)){
            this.generatorFunction = generatorFunction();
        }
        if (Objects.isNull(this.generatorFunction)){
            throw new ExchangisTaskGenerateException("Generator function is empty, please define it before generating!", null);
        }
        if (Objects.isNull(launchableExchangisJob.getExchangisJobInfo())){
            throw new ExchangisTaskGenerateException("Job info of launchableExchangisJob cannot be empty", null);
        }
        // Generate launchable exchangis job id to UUID
        launchableExchangisJob.setJobExecutionId(UUID.randomUUID().toString());
        LOG.info("Generate job execution id: [{}] for job: [{}]" , launchableExchangisJob.getJobExecutionId(), launchableExchangisJob.getExchangisJobInfo().getName());
        onEvent(new TaskGenerateInitEvent(launchableExchangisJob));
        try {
            execute(this.generatorFunction, launchableExchangisJob, getTaskGeneratorContext(), execUser);
        } catch(ErrorException e){
            if (e instanceof ExchangisTaskGenerateException){
                throw (ExchangisTaskGenerateException)e;
            }
            throw new ExchangisTaskGenerateException("Error occurred in generating progress", e);
        }
        return launchableExchangisJob;
    }

    @Override
    public LaunchableExchangisJob generate(LaunchableExchangisJob launchableExchangisJob) throws ExchangisTaskGenerateException {
        return generate(launchableExchangisJob, launchableExchangisJob.getCreateUser());
    }


    @Override
    public TaskGeneratorContext getTaskGeneratorContext() {
        return generatorContext;
    }


    /**
     * Use the default job builder manager
     * @return default manager
     */
    @Override
    public ExchangisJobBuilderManager getExchangisJobBuilderManager() {
        return new DefaultExchangisJobBuilderManager();
    }

    @Override
    public void addListener(TaskGenerateListener taskGenerateListener) {
        listeners.add(taskGenerateListener);
    }

    /**
     * Listeners listen generate event method
     * @param taskGenerateEvent event
     * @throws ExchangisTaskGenerateException
     */
    protected void onEvent(TaskGenerateEvent taskGenerateEvent) throws ExchangisTaskGenerateException{
        for (TaskGenerateListener listener : listeners) {
            try {
                listener.onEvent(taskGenerateEvent);
            } catch (ErrorException e) {
                throw new ExchangisTaskGenerateException("Fail to call 'onEvent' method in generator listener: [" + listener.getClass().getSimpleName() +
                        "] for event: [id: " + taskGenerateEvent.eventId() +", type:" + taskGenerateEvent.getClass().getSimpleName() +"]", e);
            }
        }
    }
    protected GeneratorFunction generatorFunction(){
        return (launchableExchangisJob, generatorContext) -> {
            ExchangisTaskGenerateException throwable = null;
            ExchangisJobInfo jobInfo = launchableExchangisJob.getExchangisJobInfo();
            List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>();
            if (Objects.isNull(jobInfo)){
                throwable = new ExchangisTaskGenerateException("Job information is empty in launchable exchangis job", null);
                onEvent(new TaskGenerateErrorEvent(launchableExchangisJob, throwable));
                throw throwable;
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
                    throwable = new ExchangisTaskGenerateException("Error in building launchable tasks", e);
                }
                onEvent(new TaskGenerateErrorEvent(launchableExchangisJob, throwable));
                throw throwable;
            }
            return launchableExchangisTasks;
        };
    }
    /**
     * Execute method
     * @param generatorFunction generator function
     * @param ctx context
     */
    protected abstract void execute(GeneratorFunction generatorFunction,
                                    LaunchableExchangisJob launchableExchangisJob,
                                    TaskGeneratorContext ctx, String execUser) throws ErrorException;

    @FunctionalInterface
    public interface GeneratorFunction{
        /**
         * Apply function
         * @param launchableExchangisJob origin job
         */
        List<LaunchableExchangisTask> apply(LaunchableExchangisJob launchableExchangisJob, TaskGeneratorContext ctx) throws ExchangisTaskGenerateException;
    }
}
