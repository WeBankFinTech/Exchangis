package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Task observer service
 */
@Service
public class DefaultTaskObserverService implements TaskObserverService {

    /**
     * Launchable task
     */
    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;
    @Override
    public List<LaunchableExchangisTask> onPublishLaunchableTask(int limitSize) {
        return launchableTaskDao.getTaskToLaunch(limitSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribe(LaunchableExchangisTask task) {
        LaunchedExchangisTaskEntity taskEntity = new LaunchedExchangisTaskEntity(task);
        LaunchedExchangisJobEntity jobEntity = launchedJobDao.searchLaunchedJob(task.getJobExecutionId());
        if (Objects.isNull(jobEntity) || TaskStatus.isCompleted(jobEntity.getStatus())){
            taskEntity.setStatus(jobEntity.getStatus());
            this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity);
            return false;
        } else {
            return this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity) == 1;
        }
    }
}
