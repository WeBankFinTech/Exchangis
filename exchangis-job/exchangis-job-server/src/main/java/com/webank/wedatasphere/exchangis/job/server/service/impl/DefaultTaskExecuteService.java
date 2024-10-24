package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCacheUtils;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.TaskExecuteService;
import com.webank.wedatasphere.exchangis.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class DefaultTaskExecuteService implements TaskExecuteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecuteService.class);
    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;

    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private RateLimitService rateLimitService;

    private TaskExecuteService selfService;

    @Override
    public void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent) {
        LaunchedExchangisTask task = metricsUpdateEvent.getLaunchedExchangisTask();
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        launchedTaskDao.upgradeLaunchedTaskMetrics(task.getTaskId(), Json.toJson(metricsUpdateEvent.getMetrics(), null),
                task.getLastUpdateTime());
    }

    @Override
    public void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent) throws ExchangisOnEventException {
        LaunchedExchangisTask task = statusUpdateEvent.getLaunchedExchangisTask();
        TaskStatus status = statusUpdateEvent.getUpdateStatus();
        LaunchedExchangisJobEntity launchedJob = null;
        if (!TaskStatus.isCompleted(status)){
            launchedJob = launchedJobDao.searchLaunchedJob(task.getJobExecutionId());
            TaskStatus jobStatus = TaskStatus.Cancelled;
            if (Objects.nonNull(launchedJob)){
                jobStatus = launchedJob.getStatus();
            }
            if (TaskStatus.isCompleted(jobStatus) && Objects.nonNull(task.getLauncherTask())){
                // Kill the remote task
                try {
                    task.getLauncherTask().kill();
                } catch (ExchangisTaskLaunchException e) {
                    throw new ExchangisOnEventException("Kill linkis_id: [" + task.getLinkisJobId() + "] fail", e);
                }
            }else if (jobStatus == TaskStatus.Scheduled || jobStatus == TaskStatus.Inited){
                launchedJobDao.upgradeLaunchedJobStatusInVersion(launchedJob.getJobExecutionId(), TaskStatus.Running.name(), 0, launchedJob.getLastUpdateTime());
            }
        } else {
            LaunchableExchangisTask launchableExchangisTask = task.getLaunchableExchangisTask();
            if (Objects.nonNull(launchableExchangisTask) &&
                    Objects.nonNull(launchableExchangisTask.getRateParams()) &&
                    Objects.nonNull(launchableExchangisTask.getRateParamsMap()))
                rateLimitService.releaseRateLimit(launchableExchangisTask.getRateParams(), launchableExchangisTask.getRateParamsMap());
        }
        // Have different status, then update
        if (!task.getStatus().equals(status)){
            launchedJob = Objects.isNull(launchedJob) ?
                    launchedJobDao.searchLaunchedJob(task.getJobExecutionId()) : launchedJob;
            TaskStatus jobStatus = TaskStatus.Cancelled;
            if (Objects.nonNull(launchedJob)){
                jobStatus = launchedJob.getStatus();
            }
            getSelfService().updateTaskStatus(task, status, !TaskStatus.isCompleted(jobStatus));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onLaunch(TaskLaunchEvent taskLaunchEvent) {
        LaunchedExchangisTask task = taskLaunchEvent.getLaunchedExchangisTask();
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        this.launchedTaskDao.updateLaunchInfo(task);
        // Well, search the job info
        LaunchedExchangisJobEntity launchedJob = launchedJobDao.searchLaunchedJob(task.getJobExecutionId());
        TaskStatus jobStatus = TaskStatus.Cancelled;
        if (Objects.nonNull(launchedJob)) {
            jobStatus = launchedJob.getStatus();
        }
        if (jobStatus == TaskStatus.Scheduled || jobStatus == TaskStatus.Inited) {
            // Update the job status also, status change to Running
            this.launchedJobDao.upgradeLaunchedJobStatusInVersion(task.getJobExecutionId(),
                    TaskStatus.Running.name(), 0, launchedJob.getLastUpdateTime());
        }
        JobLogCacheUtils.flush(task.getJobExecutionId(), false);
    }

    @Override
    public void onDelete(TaskDeleteEvent deleteEvent) {
        this.launchedTaskDao.deleteLaunchedTask(deleteEvent.getTaskId());
    }

    @Override
    public void onDequeue(TaskDequeueEvent dequeueEvent) throws ExchangisOnEventException {
        // Delete task in table
        this.launchableTaskDao.deleteLaunchableTask(dequeueEvent.getTaskId());
    }

    @Override
    public void onPrepare(TaskPrepareEvent prepareEvent) throws ExchangisOnEventException {
        // Update the launched task in date version
        if (this.launchedTaskDao.updateDateInVersion(prepareEvent.getTaskId(),
                prepareEvent.getUpdateTime(), prepareEvent.getVersion()) <= 0){
            throw new ExchangisOnEventException("Launched task has conflict version with current version: ["
                    + prepareEvent.getVersion() + "]", null);
        }
    }

    @Override
    public void onProgressUpdate(TaskProgressUpdateEvent updateEvent) throws ExchangisOnEventException {
        LaunchedExchangisTask task = updateEvent.getLaunchedExchangisTask();
        if (task.getProgress() != updateEvent.getProgressInfo().getProgress()) {
            getSelfService().updateTaskProgress(task, updateEvent.getProgressInfo().getProgress());
        }
    }

    /**
     * First to update task status, then update the job
     * @param task task
     * @param status status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskStatus(LaunchedExchangisTask task, TaskStatus status, boolean updateJob) throws ExchangisOnEventException {
        JobLogCacheUtils.flush(task.getJobExecutionId(), false);
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        launchedTaskDao.upgradeLaunchedTaskStatus(task.getTaskId(), status.name(), task.getLastUpdateTime());
        if (updateJob) {
            if (status == TaskStatus.Failed || status == TaskStatus.Cancelled) {
                // Update directly, no open another transaction
                launchedJobDao.upgradeLaunchedJobStatus(task.getJobExecutionId(), status.name(), task.getLastUpdateTime());
            } else if (status == TaskStatus.Success) {
                getSelfService().updateJobStatus(task.getJobExecutionId(), TaskStatus.Success, task.getLastUpdateTime());
            }
        }
    }

    @Override
    public void updateTaskProgress(LaunchedExchangisTask task, float progress) throws ExchangisOnEventException {
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        this.launchedTaskDao.upgradeLaunchedTaskProgress(task.getTaskId(), progress, task.getLastUpdateTime());
        getSelfService().updateJobProgress(task.getJobExecutionId(), task.getLastUpdateTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobProgress(String jobExecutionId, Date updateTime) {
        // Sum all the task's progress
        float totalTaskProgress = this.launchedTaskDao.sumProgressByJobExecutionId(jobExecutionId);
        if (totalTaskProgress > 0){
           this.launchedJobDao.upgradeLaunchedJobProgress(jobExecutionId, totalTaskProgress, updateTime);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatus(String jobExecutionId, TaskStatus status, Date updateTime) {
        List<String> statusList = launchedTaskDao.selectTaskStatusByJobExecutionId(jobExecutionId);
        if (statusList.stream().allMatch(taskStatus -> taskStatus.equalsIgnoreCase(TaskStatus.Success.name()))){
            launchedJobDao.upgradeLaunchedJobStatusInVersion(jobExecutionId,
                    TaskStatus.Success.name(), statusList.size(), updateTime);
        }
    }

    /**
     * Get the self service
     * @return service
     */
    private TaskExecuteService getSelfService() throws ExchangisOnEventException {
        if (Objects.isNull(selfService)){
            this.selfService = SpringContextHolder.getBean(TaskExecuteService.class);
            if (Objects.isNull(this.selfService)){
                throw new ExchangisOnEventException("TaskExecuteService cannot be found in spring context", null);
            }
        }
        return this.selfService;
    }
}
