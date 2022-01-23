package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DefaultTaskObserverService implements TaskObserverService {

    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Override
    public List<LaunchableExchangisTask> onPublishLaunchableTask(int limitSize) {
        return launchableTaskDao.getTaskToLaunch(limitSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribe(LaunchableExchangisTask task) {
        LaunchedExchangisTaskEntity taskEntity = new LaunchedExchangisTaskEntity(task);
        int result = this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity);
        return result <= 1;
    }
}
