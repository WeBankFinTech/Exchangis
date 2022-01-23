package com.webank.wedatasphere.exchangis.job.launcher.linkis;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisTaskNotExistException;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskLog;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskLogQuery;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskProgressInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import org.apache.linkis.computation.client.once.SubmittableOnceJob;
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob;
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder;
import org.apache.linkis.computation.client.operator.impl.*;
import org.apache.linkis.computation.client.utils.LabelKeyUtils;
import org.apache.linkis.protocol.engine.JobProgressInfo;

import java.util.*;

import static com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration.*;

/**
 * Linkis launcher task
 */
public class LinkisLauncherTask implements AccessibleLauncherTask {

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

    static{

    }
    public static LinkisLauncherTask init(LaunchableExchangisTask task, Map<String, String> engineVersions){
        return new LinkisLauncherTask(task, engineVersions);
    }

    public static LinkisLauncherTask init(String jobId, String user, Map<String, Object> jobInfo, String engineConn){
        return new LinkisLauncherTask(jobId, user, jobInfo, engineConn);
    }

    private LinkisLauncherTask(LaunchableExchangisTask task, Map<String, String> engineVersions){
        this.onceJob = toSubmittableJob(task);
        this.engineVersions = engineVersions;
    }

    private LinkisLauncherTask(String jobId, String user, Map<String, Object> jobInfo, String engineConn){
        // Build existing job
        this.onceJob = SimpleOnceJob.build(jobId, user);
        this.jobId = jobId;
        this.jobInfo = jobInfo;
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
                } else if ("failed".equalsIgnoreCase(linkisJobStatus)) {
                    this.status = TaskStatus.Failed;
                } else {
                    this.status = TaskStatus.Running;
                }
            } catch (Exception e){
                try {
                    dealException(e);
                } catch (ExchangisTaskNotExistException ne){
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
                    this.metricsOperator.apply();
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
                   EngineConnProgressInfo progressInfo =  this.progressOperator.apply();
                   JobProgressInfo[] progressInfoArray = progressInfo.progressInfo();
                   TaskProgressInfo taskProgressInfo = new TaskProgressInfo();
                   if (progressInfoArray.length > 1){
                        taskProgressInfo.setTotal(progressInfoArray[0].totalTasks());
                        taskProgressInfo.setFailed(progressInfoArray[0].failedTasks());
                        taskProgressInfo.setRunning(progressInfoArray[0].runningTasks());
                        taskProgressInfo.setSucceed(progressInfoArray[0].succeedTasks());
                   }
                   taskProgressInfo.setProgress(progressInfo.progress());
                } else if (taskStatus == TaskStatus.Success){
                    if (Objects.nonNull(this.progressInfo)){
                        this.progressInfo = new TaskProgressInfo();
                    }
                    this.progressInfo.setProgress(1.0f);
                }
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
            }catch(Exception e){
                dealException(e);
            }
        }
    }

    @Override
    public TaskLog queryLogs(TaskLogQuery query) throws ExchangisTaskLaunchException {
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
                logOperator.setLastRows(query.getLastRows());
                EngineConnLogs logs = logOperator.apply();
                boolean isEnd = logs.logs().size() <= 0;
                if (isEnd){
                    isEnd = TaskStatus.isCompleted(getStatus());
                }
                return new TaskLog(logs.endLine(), isEnd, logs.logs());
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
            // New the operators for job
            prepareOperators(this.onceJob);
        } catch (Exception e){
            dealException(e);
        }
    }

    /**
     * Convert launchable task to once job
     * @param task task
     * @return once job
     */
    private SimpleOnceJob toSubmittableJob(LaunchableExchangisTask task){
        //TODO deal the start up params
        SimpleOnceJobBuilder jobBuilder = SimpleOnceJob.builder().setCreateService("")
                .addLabel(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY(), task.getEngineType().toLowerCase() + "-" +
                        engineVersions.getOrDefault(task.getEngineType().toLowerCase(), "0.0.0"))
                .addLabel(LabelKeyUtils.USER_CREATOR_LABEL_KEY(), task.getExecuteUser() + "-" + LAUNCHER_LINKIS_CREATOR.getValue())
                .addLabel(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY(), LAUNCHER_LINKIS_ENGINE_CONN_MODE.getValue())
                .setMaxSubmitTime(LAUNCHER_MAX_SUBMIT.getValue())
                .addExecuteUser(task.getExecuteUser());
        Optional.ofNullable(task.getLinkisContentMap()).ifPresent(params -> params.forEach(jobBuilder::addJobContent));
        Optional.ofNullable(task.getLinkisParamsMap()).ifPresent(params -> params.forEach(jobBuilder::addRuntimeParam));
        return jobBuilder.build();
    }

    private void prepareOperators(SimpleOnceJob onceJob){
        this.progressOperator = (EngineConnProgressOperator) onceJob.getOperator(EngineConnProgressOperator.OPERATOR_NAME());
        this.metricsOperator = (EngineConnMetricsOperator) onceJob.getOperator(EngineConnMetricsOperator.OPERATOR_NAME());
    }
    /**
     * Deal exception
     * @param e
     * @throws ExchangisTaskLaunchException
     */
    private void dealException(Exception e) throws ExchangisTaskLaunchException {
        String message = e.getMessage();
        if (message.contains(TASK_NOT_EXIST)){
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
                this.jobInfo = onceJob.getNodeInfo();
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
