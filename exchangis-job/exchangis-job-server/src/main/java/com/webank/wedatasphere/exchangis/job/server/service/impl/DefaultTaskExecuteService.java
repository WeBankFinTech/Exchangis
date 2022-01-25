package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;
import com.webank.wedatasphere.exchangis.job.server.service.TaskExecuteService;
import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultTaskExecuteService implements TaskExecuteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecuteService.class);
    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;

    @Override
    public void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent) {
        LaunchedExchangisTask task = metricsUpdateEvent.getLaunchedExchangisTask();
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        launchedTaskDao.upgradeLaunchedTaskMetrics(Json.toJson(metricsUpdateEvent.getMetrics(), null),
                task.getTaskId(), task.getLastUpdateTime());
    }

    @Override
    public void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent) throws ExchangisOnEventException {
        LaunchedExchangisTask task = statusUpdateEvent.getLaunchedExchangisTask();
        TaskStatus status = statusUpdateEvent.getUpdateStatus();
        Calendar calendar = Calendar.getInstance();
        if (!TaskStatus.isCompleted(status)){
            LaunchedExchangisJobEntity launchedJob = launchedJobDao.searchLaunchedJob(task.getJobExecutionId());
            TaskStatus jobStatus = launchedJob.getStatus();
            if (TaskStatus.isCompleted(jobStatus) && Objects.nonNull(task.getLauncherTask())){
                // Kill the remote task
                try {
                    task.getLauncherTask().kill();
                } catch (ExchangisTaskLaunchException e) {
                    throw new ExchangisOnEventException("Kill linkis_id: [" + task.getLinkisJobId() + "] fail", e);
                }
            }else if (jobStatus == TaskStatus.Scheduled || jobStatus == TaskStatus.Inited){
                launchedJobDao.upgradeLaunchedJobStatus(launchedJob.getJobExecutionId(), TaskStatus.Running.name(), calendar.getTime());
            }
        }
        // Have different status, then update
        if (!task.getStatus().equals(status)){
            TaskExecuteService taskExecuteService = SpringContextHolder.getBean(TaskExecuteService.class);
            if (Objects.isNull(taskExecuteService)){
                throw new ExchangisOnEventException("TaskExecuteService cannot be found in spring context", null);
            }
            taskExecuteService.updateTaskStatus(task, status);
        }
    }

    @Override
    public void onInfoUpdate(TaskInfoUpdateEvent infoUpdateEvent) {
        LaunchedExchangisTask task = infoUpdateEvent.getLaunchedExchangisTask();
        task.setLastUpdateTime(Calendar.getInstance().getTime());
        this.launchedTaskDao.updateLaunchInfo(task);
    }

    @Override
    public void onDelete(TaskDeleteEvent deleteEvent) {
        this.launchedTaskDao.deleteLaunchedTask(deleteEvent.getTaskId());
    }

    @Override
    public void onProgressUpdate(TaskProgressUpdateEvent updateEvent) {
        LaunchedExchangisTask task = updateEvent.getLaunchedExchangisTask();
        if (task.getProgress() != updateEvent.getProgressInfo().getProgress()) {
            task.setLastUpdateTime(Calendar.getInstance().getTime());
            this.launchedTaskDao.upgradeLaunchedTaskProgress(task.getTaskId(), updateEvent.getProgressInfo().getProgress(), task.getLastUpdateTime());
        }
    }

    /**
     * First to update task status, then update the job(don't use transaction)
     * @param task task
     * @param status status
     */
    @Override
    public void updateTaskStatus(LaunchedExchangisTask task, TaskStatus status) {
        Calendar calendar = Calendar.getInstance();
        task.setLastUpdateTime(calendar.getTime());
        launchedTaskDao.upgradeLaunchedTaskStatus(task.getTaskId(), status.name(), task.getLastUpdateTime());
        if (status == TaskStatus.Failed || status == TaskStatus.Cancelled){
            launchedJobDao.upgradeLaunchedJobStatus(task.getJobExecutionId(), TaskStatus.Failed.name(), task.getLastUpdateTime());
        } else if (status == TaskStatus.Success){
            List<String> statusList = launchedTaskDao.selectTaskStatusByJobExecutionId(task.getJobExecutionId());
            if (statusList.stream().allMatch(taskStatus -> taskStatus.equalsIgnoreCase(TaskStatus.Success.name()))){
                launchedJobDao.upgradeLaunchedJobStatus(task.getJobExecutionId(), TaskStatus.Success.name(), task.getLastUpdateTime());
            }
        }

    }
}
