package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Task observer service
 */
@Service
public class DefaultTaskObserverService implements TaskObserverService {

    private static Logger LOG = LoggerFactory.getLogger(TaskObserverService.class);

    private static final CommonVars<String> TASK_OBSERVER_LAUNCH_DELAY_IN_SEC = CommonVars.apply(
            "wds.exchangis.job.task.observer.launch.delay-in-sec", "10,20,30,60");
    /**
     * Launchable task
     */
    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;

    /**
     * Launch delay in seconds
     */
    private int[] launchDelayInSec;

    public DefaultTaskObserverService(){
        String delayDefine = TASK_OBSERVER_LAUNCH_DELAY_IN_SEC.getValue();
        if (StringUtils.isNotBlank(delayDefine)){
            String[] delayParts = delayDefine.split(",");
            try {
                int[] delayInSec = new int[delayParts.length];
                for (int i = 0; i < delayInSec.length; i ++ ){
                    delayInSec[i] = Integer.parseInt(delayParts[i]);
                }
                this.launchDelayInSec = delayInSec;
            }catch (Exception e){
                LOG.warn("The wrong format in launch delay definition: [{}]", delayDefine, e);
            }
        }
    }
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
            // Add the job id
            taskEntity.setJobId(jobEntity.getJobId());
            return this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity) == 1;
        }
    }

    @Override
    public void unsubscribe(LaunchableExchangisTask task) {
        LOG.info("Unsubscribe launch task: [{}], id: [{}], execution_id: [{}], last_update_time: [{}]",
                task.getName(), task.getId(), task.getJobExecutionId(), task.getLastUpdateTime());
        this.launchedTaskDao.deleteLaunchedTaskInVersion(task.getId() + "", task.getLastUpdateTime());
    }

    @Override
    public void discard(List<LaunchableExchangisTask> tasks) {
        for(int i = 0; i < tasks.size(); i ++){
            // Delay for each task
            Calendar.getInstance();
//            this.launchableTaskDao.delayLaunchableTask();
        }
    }

}
