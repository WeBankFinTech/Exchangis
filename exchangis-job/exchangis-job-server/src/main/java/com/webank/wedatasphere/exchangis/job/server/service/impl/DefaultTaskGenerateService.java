package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateErrorEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateInitEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateSuccessEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.NewInTaskObserver;
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCacheUtils;
import com.webank.wedatasphere.exchangis.job.server.service.TaskGenerateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * Task generate service
 */
@Service
public class DefaultTaskGenerateService implements TaskGenerateService {
    @Resource
    private LaunchedJobDao launchedJobDao;

    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private NewInTaskObserver newInTaskObserver;
    @Override
    public void onError(TaskGenerateErrorEvent errorEvent) {
        JobLogCacheUtils.flush(errorEvent.getLaunchableExchangisJob().getJobExecutionId(), true);
        this.launchedJobDao.upgradeLaunchedJobStatus(errorEvent.getLaunchableExchangisJob().getJobExecutionId()
                , TaskStatus.Failed.name(), Calendar.getInstance().getTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onInit(TaskGenerateInitEvent initEvent) {
        LaunchableExchangisJob job = initEvent.getLaunchableExchangisJob();
        LaunchedExchangisJobEntity launchedJob = new LaunchedExchangisJobEntity(job);
        launchedJobDao.insertLaunchedJob(launchedJob);
        JobLogCacheUtils.flush(job.getJobExecutionId(), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onSuccess(TaskGenerateSuccessEvent successEvent) {
        LaunchableExchangisJob launchableExchangisJob = successEvent.getLaunchableExchangisJob();
        List<LaunchableExchangisTask> tasks = successEvent.getTaskGenerated();
        Calendar calendar = Calendar.getInstance();
        tasks.forEach( task -> {
            task.setJobExecutionId(launchableExchangisJob.getJobExecutionId());
            task.setCreateTime(calendar.getTime());
            task.setLastUpdateTime(task.getCreateTime());
        });
        this.launchableTaskDao.addLaunchableTask(tasks);
        LaunchedExchangisJobEntity launchedJob = new LaunchedExchangisJobEntity(launchableExchangisJob);
        launchedJob.setStatus(TaskStatus.Scheduled);
        launchedJob.setLaunchableTaskNum(tasks.size());
        launchedJob.setLastUpdateTime(calendar.getTime());
        this.launchedJobDao.updateLaunchInfo(launchedJob);
        // Offer to the observer
        tasks.forEach(task -> this.newInTaskObserver.getCacheQueue().offer(task));
        JobLogCacheUtils.flush(launchableExchangisJob.getJobExecutionId(), false);
    }
}
