package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.execution.events.*;
import com.webank.wedatasphere.exchangis.job.server.service.TaskExecuteService;
import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DefaultTaskExecuteService implements TaskExecuteService {

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;

    @Override
    public void onMetricsUpdate(TaskMetricsUpdateEvent metricsUpdateEvent) {
        launchedTaskDao.upgradeLaunchedTaskMetrics(Json.toJson(metricsUpdateEvent.getMetrics(), null),
                metricsUpdateEvent.getLaunchedExchangisTask().getTaskId());
    }

    @Override
    public void onStatusUpdate(TaskStatusUpdateEvent statusUpdateEvent) {
        LaunchedExchangisTask task = statusUpdateEvent.getLaunchedExchangisTask();
//        if (statusUpdateEvent){
//
//        }
        LaunchedExchangisJobEntity launchedJob = launchedJobDao.searchLaunchedJob(task.getJobExecutionId());
        if (TaskStatus.isCompleted(launchedJob.getStatus())){
        }
    }

    @Override
    public void onInfoUpdate(TaskInfoUpdateEvent infoUpdateEvent) {

    }

    @Override
    public void onDelete(TaskDeleteEvent deleteEvent) {

    }

    @Override
    public void onProgressUpdate(TaskProgressUpdateEvent updateEvent) {

    }
}
