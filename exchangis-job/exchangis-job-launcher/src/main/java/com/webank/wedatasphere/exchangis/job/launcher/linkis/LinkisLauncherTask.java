package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskNotExistException;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.computation.client.LinkisJobBuilder;
import org.apache.linkis.computation.client.LinkisJobClient;
import org.apache.linkis.computation.client.once.SubmittableOnceJob;
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob;
import org.apache.linkis.computation.client.once.simple.SubmittableSimpleOnceJob;
import org.apache.linkis.computation.client.operator.impl.*;
import org.apache.linkis.computation.client.utils.LabelKeyUtils;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;
import org.apache.linkis.datasourcemanager.common.util.PatternInjectUtils;
import org.apache.linkis.protocol.engine.JobProgressInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration.*;

/**
 * Linkis launcher task
 */
public class LinkisLauncherTask implements AccessibleLauncherTask {

    private static final Logger LOG = LoggerFactory.getLogger(LinkisLauncherTask.class);

    /**
     * Engine versions
     */
    private Map<String, String> engineVersions = new HashMap<>();

    /**
     * Linkis Job information
     */
    private Map<String, Object> jobInfo = new HashMap<>();

    /**
     * Linkis job id
     */
    private String jobId;

    /**
     * Engine Conn type
     */
    private String engineConn;
    /**
     * Refer to linkis job status
     */
    private TaskStatus status = TaskStatus.Undefined;

    /**
     * Progress
     */
    private TaskProgressInfo progressInfo;
    /**
     * Hold the once job instance
     */
    private SimpleOnceJob onceJob;

    /**
     * Progress operator
     */
    private EngineConnProgressOperator progressOperator;

    /**
     * Metrics operator
     */
    private EngineConnMetricsOperator metricsOperator;

    /**
     * Request error count
     */
    private AtomicInteger reqError = new AtomicInteger(0);

    static{

    }
    public static LinkisLauncherTask init(LaunchableExchangisTask task, Map<String, String> engineVersions){
        return new LinkisLauncherTask(task, engineVersions);
    }

    public static LinkisLauncherTask init(String jobId, String user, Map<String, Object> jobInfo, String engineConn){
        return new LinkisLauncherTask(jobId, user, jobInfo, engineConn);
    }

    private LinkisLauncherTask(LaunchableExchangisTask task, Map<String, String> engineVersions){
        this.engineVersions = engineVersions;
        this.onceJob = toSubmittableJob(task);
    }

    private LinkisLauncherTask(String jobId, String user, Map<String, Object> jobInfo, String engineConn){
        // Build existing job
        this.onceJob = LinkisJobClient.once().simple().build(jobId, user);
        this.jobId = jobId;
        this.jobInfo = jobInfo;
        if (StringUtils.isNotBlank(engineConn)){
            // To lower case
            engineConn = engineConn.toLowerCase();
        }
        this.engineConn = engineConn;
        prepareOperators(this.onceJob);
    }

    @Override
    public TaskStatus getStatus() throws ExchangisTaskLaunchException {
        if (Objects.nonNull(this.onceJob)) {
            if (TaskStatus.isCompleted(this.status)){
                return status;
            }
            try {
                // Fetch the latest info
                getJobInfo(true);
                String linkisJobStatus = this.onceJob.getStatus(this.jobInfo);
                if ("success".equalsIgnoreCase(linkisJobStatus)) {
                    this.status = TaskStatus.Success;
                } else if ("failed".equalsIgnoreCase(linkisJobStatus)){
                    this.status = TaskStatus.Failed;
                } else if ("shuttingdown".equalsIgnoreCase(linkisJobStatus)) {
                    LOG.warn("Will retry on linkis job status: [{}]", linkisJobStatus);
                    // Retry on shutting down status
                    this.status = TaskStatus.WaitForRetry;
                } else {
                    this.status = TaskStatus.Running;
                }
                // Init the error count
                this.reqError.set(0);
            } catch (Exception e){
                try {
                    dealException(e);
                } catch (ExchangisTaskNotExistException ne){
                    LOG.warn("Not find the launcher task in exchangis", e);
                    this.status = TaskStatus.Failed;
                }
            }
        }
        return this.status;
    }

    @Override
    public TaskStatus getLocalStatus() {
        return this.status;
    }

    @Override
    public Map<String, Object> getMetricsInfo() throws ExchangisTaskLaunchException {
        if (Objects.nonNull(this.metricsOperator)){
            try{
                // Invoke getStatus() to get real time status
                if(!TaskStatus.isCompleted(getStatus())){
                    Map<String, Object> metrics = (Map<String, Object>)this.metricsOperator.apply();
                    // Init the error count
                    this.reqError.set(0);
                    return metrics;
                }
            }catch(Exception e){
                dealException(e);
            }
        }
        return null;
    }

    @Override
    public TaskProgressInfo getProgressInfo() throws ExchangisTaskLaunchException {
        if (Objects.nonNull(this.progressOperator)){
            try {
                // invoke getStatus() to get real time status
                TaskStatus taskStatus = getStatus();
                if (!TaskStatus.isCompleted(taskStatus)){
                   EngineConnProgressInfo progressInfo =  (EngineConnProgressInfo)this.progressOperator.apply();
                   JobProgressInfo[] progressInfoArray = progressInfo.progressInfo();
                   TaskProgressInfo taskProgressInfo = new TaskProgressInfo();
                   if (progressInfoArray.length > 1){
                        taskProgressInfo.setTotal(progressInfoArray[0].totalTasks());
                        taskProgressInfo.setFailed(progressInfoArray[0].failedTasks());
                        taskProgressInfo.setRunning(progressInfoArray[0].runningTasks());
                        taskProgressInfo.setSucceed(progressInfoArray[0].succeedTasks());
                   }
                   taskProgressInfo.setProgress(progressInfo.progress());
                   this.progressInfo = taskProgressInfo;
                } else if (taskStatus == TaskStatus.Success){
                    if (Objects.isNull(this.progressInfo)){
                        this.progressInfo = new TaskProgressInfo();
                    }
                    this.progressInfo.setProgress(1.0f);
                }
                // Init the error count
                this.reqError.set(0);
            } catch(Exception e){
                dealException(e);
            }
        }
        return this.progressInfo;
    }

    @Override
    public void kill() throws ExchangisTaskLaunchException {
        if (Objects.nonNull(this.onceJob)){
            try{
                this.onceJob.kill();
                this.status = TaskStatus.Cancelled;
                // Init the error count
                this.reqError.set(0);
            }catch(Exception e){
                dealException(e);
            }
        }
    }

    @Override
    public LogResult queryLogs(LogQuery query) throws ExchangisTaskLaunchException {
        // The logOperator is not thread safe, so create it each time
        if (Objects.nonNull(this.onceJob)){
            try{
                EngineConnLogOperator logOperator = (EngineConnLogOperator) this.onceJob.getOperator(EngineConnLogOperator.OPERATOR_NAME());
                logOperator.setFromLine(query.getFromLine());
                logOperator.setPageSize(query.getPageSize());
                logOperator.setEngineConnType(this.engineConn);
                logOperator.setECMServiceInstance(this.onceJob.getECMServiceInstance(this.jobInfo));
                logOperator.setIgnoreKeywords(query.getIgnoreKeywords());
                logOperator.setOnlyKeywords(query.getOnlyKeywords());
                if (Objects.nonNull(query.getLastRows())){
                    logOperator.setLastRows(query.getLastRows());
                }
                EngineConnLogs logs = (EngineConnLogs)logOperator.apply();
                boolean isEnd = logs.logs().size() <= 0;
                if (isEnd){
                    isEnd = TaskStatus.isCompleted(getStatus());
                }
                // Init the error count
                this.reqError.set(0);
                return new LogResult(logs.endLine(), isEnd, logs.logs());
            } catch (Exception e){
                dealException(e);
            }
        }

        return null;
    }

    @Override
    public synchronized void submit() throws ExchangisTaskLaunchException {
        if (Objects.isNull(this.onceJob) || !(this.onceJob instanceof SubmittableOnceJob)){
            throw new ExchangisTaskLaunchException("Unsupported 'submit' method", null);
        }
        try {
            ((SubmittableOnceJob) this.onceJob).submit();
            TaskStatus status = getStatus();
            if (status == TaskStatus.Undefined || status == TaskStatus.WaitForRetry){
                throw new ExchangisTaskLaunchException("Fail to submit to linkis server with unexpected final status： [" + status + "]", null);
            }
            // New the operators for job
            prepareOperators(this.onceJob);
            Map<String, Object> jobInfo = getJobInfo(false);
            jobInfo.put("ecmServiceInstance", ((SubmittableSimpleOnceJob)this.onceJob).getECMServiceInstance());
        } catch (Exception e){
            dealException(e);
        }
    }

    /**
     * Convert launchable task to once job
     * @param task task
     * @return once job
     */
    @SuppressWarnings("unchecked")
    private SimpleOnceJob toSubmittableJob(LaunchableExchangisTask task){
        //TODO deal the start up params
        LinkisJobBuilder<SubmittableSimpleOnceJob> jobBuilder = LinkisJobClient.once().simple().builder().setCreateService(LAUNCHER_LINKIS_CREATOR.getValue())
                .setMaxSubmitTime(LAUNCHER_LINKIS_MAX_SUBMIT.getValue())
                .addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), task.getEngineType().toLowerCase() + "-" +
                        engineVersions.getOrDefault(task.getEngineType().toLowerCase(), "0.0.0"))
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), task.getExecuteUser() + "-" + LAUNCHER_LINKIS_CREATOR.getValue())
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), LAUNCHER_LINKIS_ENGINE_CONN_MODE.getValue())
                .addExecuteUser(task.getExecuteUser());
        Optional.ofNullable(task.getLinkisContentMap()).ifPresent(params -> params.forEach(jobBuilder::addJobContent));
        Optional.ofNullable(task.getLinkisParamsMap()).ifPresent(params -> {
            Object runtimeParams = params.get(LAUNCHER_LINKIS_RUNTIME_PARAM_NAME);
            if (Objects.nonNull(runtimeParams) && runtimeParams instanceof Map){
                jobBuilder.setRuntimeParams((Map<String, Object>) runtimeParams);
            }
            Object startupParams = params.get(LAUNCHER_LINKIS_STARTUP_PARAM_NAME);
            if (Objects.nonNull(startupParams) && startupParams instanceof Map){
                jobBuilder.setStartupParams((Map<String, Object>) startupParams);
            }
        });
        try {
            jobBuilder.addStartupParam(PatternInjectUtils.inject(LAUNCHER_LINKIS_EXEC_ID,
                    new String[]{task.getEngineType().toLowerCase(Locale.ROOT)}), task.getId());
        } catch (JsonErrorException e) {
            //Ignore
        }
        return jobBuilder.build();
    }

    private void prepareOperators(SimpleOnceJob onceJob){
        this.progressOperator = (EngineConnProgressOperator) onceJob.getOperator(EngineConnProgressOperator.OPERATOR_NAME());
        this.metricsOperator = (EngineConnMetricsOperator) onceJob.getOperator(EngineConnMetricsOperator.OPERATOR_NAME());
    }
    /**
     * Deal exception
     * @param e exception entity
     * @throws ExchangisTaskLaunchException
     */
    private void dealException(Exception e) throws ExchangisTaskLaunchException {
        String message = e.getMessage();
        if (reqError.incrementAndGet() > LAUNCHER_LINKIS_MAX_ERROR.getValue()){
            this.status = TaskStatus.Failed;
            LOG.info("Error to connect to the linkis server over {} times, linkis_id: {}, now to mark the task status: {}", LAUNCHER_LINKIS_CREATOR.getValue(), this.jobId, this.status, e);
            return;
        }
        if (StringUtils.isNotBlank(message) && message.contains(TASK_NOT_EXIST)){
            throw new ExchangisTaskNotExistException("It seems that the linkis job: [ linkis_id: " + getJobId() + "] cannot be found in linkis server", e);
        } else{
            throw new ExchangisTaskLaunchException("Unexpected exception in communicating with linkis server", e);
        }
    }
    /**
     * Get the linkis's job information
     * @return info map
     */
    public Map<String, Object> getJobInfo(boolean refresh){
        if (Objects.nonNull(onceJob)){
            if (Objects.isNull(this.jobInfo) || refresh){
                this.jobInfo = this.onceJob.getNodeInfo();
            }
        }
        return this.jobInfo;
    }

    /**
     * Get the linkis's job id
     * @return id
     */
    public String getJobId(){
        if (Objects.nonNull(onceJob)){
             this.jobId = onceJob.getId();
        }
        return this.jobId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
