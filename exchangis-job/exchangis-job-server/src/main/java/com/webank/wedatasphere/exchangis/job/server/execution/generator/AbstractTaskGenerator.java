package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.builder.manager.DefaultExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.builder.manager.ExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent;
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

    private List<TaskGenerateListener> listeners = new ArrayList<>();

    protected TaskGeneratorContext generatorContext;

    @Override
    public void init() throws ExchangisJobException {
    }

    @Override
    public LaunchableExchangisJob init(ExchangisJobInfo jobInfo) throws ExchangisTaskGenerateException {
        Calendar calendar = Calendar.getInstance();
        LaunchableExchangisJob launchableExchangisJob = new LaunchableExchangisJob();
        launchableExchangisJob.setExchangisJobInfo(jobInfo);
        launchableExchangisJob.setName(jobInfo.getName());
        launchableExchangisJob.setEngineType(jobInfo.getEngineType());
        launchableExchangisJob.setJobLabel(jobInfo.getJobLabel());
        launchableExchangisJob.setCreateTime(calendar.getTime());
        launchableExchangisJob.setLastUpdateTime(calendar.getTime());
        launchableExchangisJob.setId(jobInfo.getId());
        launchableExchangisJob.setExecUser(jobInfo.getExecuteUser());
        launchableExchangisJob.setCreateUser(jobInfo.getCreateUser());
        // Generate launchable exchangis job id to UUID
        launchableExchangisJob.setJobExecutionId(UUID.randomUUID().toString());
        LOG.info("Generate job execution id: [{}] for job: [{}]" , launchableExchangisJob.getJobExecutionId(), launchableExchangisJob.getExchangisJobInfo().getName());
        onEvent(new TaskGenerateInitEvent(launchableExchangisJob));
        return launchableExchangisJob;
    }

    @Override
    public LaunchableExchangisJob generate(LaunchableExchangisJob launchableExchangisJob, String tenancy) throws ExchangisTaskGenerateException {
        if (Objects.isNull(launchableExchangisJob.getExchangisJobInfo())){
            throw new ExchangisTaskGenerateException("Job info of launchableExchangisJob cannot be empty", null);
        }
        launchableExchangisJob.setCreateUser(tenancy);
        try {
            execute(launchableExchangisJob, getTaskGeneratorContext(), tenancy);
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
        if (taskGenerateEvent instanceof TaskGenerateInitEvent){
            info(taskGenerateEvent.getLaunchableExchangisJob(), "Init to create launched job and begin generating");
        } else if (taskGenerateEvent instanceof TaskGenerateSuccessEvent){
            info(taskGenerateEvent.getLaunchableExchangisJob(), "Success to generate launched job, output tasks [{}]",
                    taskGenerateEvent.getLaunchableExchangisJob().getLaunchableExchangisTasks().size());
        } else if (taskGenerateEvent instanceof TaskGenerateErrorEvent){
            error(taskGenerateEvent.getLaunchableExchangisJob(), "Error occurred in generating",
                    ((TaskGenerateErrorEvent)taskGenerateEvent).getException());
        }
    }

    @Override
    public JobLogListener getJobLogListener() {
        return getTaskGeneratorContext().getJobLogListener();
    }

    @Override
    public JobLogEvent getJobLogEvent(JobLogEvent.Level level, LaunchableExchangisJob job, String message, Object... args) {
        return new JobLogEvent(level, job.getCreateUser(), job.getJobExecutionId(), message, args);
    }


    protected abstract void execute(LaunchableExchangisJob launchableExchangisJob, TaskGeneratorContext ctx, String tenancy) throws ErrorException;
}
