package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.AccessibleLauncherTask;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLauncher;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.AbstractTaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.DefaultTaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskManager;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.loadbalance.TaskSchedulerLoadBalancer;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TenancyParallelGroupFactory;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.LoadBalanceSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.subscriber.AbstractTaskObserver;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.TaskObserverService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR;

/**
 * Task observer service
 */
@Service
public class DefaultTaskObserverService implements TaskObserverService {

    private static Logger LOG = LoggerFactory.getLogger(TaskObserverService.class);

    private static final CommonVars<String> TASK_OBSERVER_LAUNCH_DELAY_IN_SEC = CommonVars.apply(
            "wds.exchangis.job.task.observer.subscribe.delay-in-sec", "10,20,30,60");
    /**
     * Launchable task
     */
    @Resource
    private LaunchableTaskDao launchableTaskDao;

    @Resource
    private LaunchedTaskDao launchedTaskDao;

    @Resource
    private LaunchedJobDao launchedJobDao;

    @Autowired
    private JobInfoService jobInfoService;
    /**
     * Task generator
     */
    @Resource
    private TaskGenerator<LaunchableExchangisJob> taskGenerator;

    /**
     * Task execution
     */
    private AbstractTaskExecution taskExecution;


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
        } else {
            launchDelayInSec = new int[]{10};
        }
    }
    @Override
    public List<LaunchableExchangisTask> onPublishLaunchAbleTask(String instance, int limitSize) {
        // Get the launch-able task from launch-able task inner join launched task
        return launchableTaskDao.getTaskToLaunch(instance, limitSize);
    }

    @Override
    public List<LaunchableExchangisTask> onPublishLaunchAbleTaskInExpire(String instance, Date expireTime, int limitSize) {
        return launchableTaskDao.getTaskToLaunchInExpire(instance, TaskStatus.Scheduled.name()
                , expireTime, limitSize);
    }

    @Override
    public List<LaunchedExchangisTaskEntity> onPublishLaunchedTaskInExpire(String instance, Date expireTime, int limitSize) {
        return launchedTaskDao.getLaunchedTaskInExpire(instance, TaskStatus.Running.name(),
                expireTime, limitSize);
    }

    @Override
    public List<LaunchedExchangisJobEntity> onPublishLaunchedJobInExpire(String instance, Date expireTime, int limitSize) {
        return launchedJobDao.getLaunchedJobInExpire(instance, TaskStatus.Inited.name(),
                expireTime, limitSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribe(LaunchableExchangisTask task) {
        // Fetch all message from database
        LaunchableExchangisTask lastTask = launchableTaskDao.getLaunchableTask(task.getId() + "");
        if (Objects.nonNull(lastTask)) {
            LaunchedExchangisTaskEntity taskEntity = new LaunchedExchangisTaskEntity(lastTask);
            LaunchedExchangisJobEntity jobEntity = launchedJobDao.searchLaunchedJob(lastTask.getJobExecutionId());
            if (Objects.isNull(jobEntity) || TaskStatus.isCompleted(jobEntity.getStatus())) {
                boolean dequeue = true;
                if (Objects.nonNull(jobEntity)) {
                    taskEntity.setStatus(jobEntity.getStatus());
                    dequeue = this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity) == 1;
                }
                if (dequeue) {
                    try {
                        this.launchableTaskDao.deleteLaunchableTask(task.getId() + "");
                    } catch (Exception e) {
                        LOG.warn("Internal_Error: Error in deleting launch-able task: [id: {}, execution_id: {}] for job is completed with status: [{}]",
                                lastTask.getId(), lastTask.getJobExecutionId(), jobEntity.getStatus(), e);
                    }
                }
                return false;
            } else {
                // Add the job id
                taskEntity.setJobId(jobEntity.getJobId());
                boolean result = this.launchedTaskDao.insertLaunchedTaskOrUpdate(taskEntity) == 1;
                if (result){
                    // Set the linkis message
                    task.setLinkisJobContent(lastTask.getLinkisJobContent());
                    task.setLinkisParams(lastTask.getLinkisParams());
                    task.setLinkisSource(lastTask.getLinkisSource());
                    // set the commit version as 0
                    task.setCommitVersion(0);
                }
                return result;
            }
        }
        return false;
    }

    @Override
    public boolean subscribe(LaunchedExchangisTaskEntity task) {
        // Build launched task for connecting remote
        LaunchedExchangisTask launchedTask = new LaunchedExchangisTask();
        launchedTask.setId(task.getId());
        launchedTask.setTaskId(task.getTaskId());
        launchedTask.setJobExecutionId(task.getJobExecutionId());
        launchedTask.setInstance(task.getInstance());
        launchedTask.setStatus(task.getStatus());
        launchedTask.setLinkisJobId(task.getLinkisJobId());
        launchedTask.setLinkisJobInfo(task.getLinkisJobInfo());
        launchedTask.setExecuteUser(task.getExecuteUser());
        launchedTask.setEngineType(task.getEngineType());
        launchedTask.setErrorCode(task.getErrorCode());
        launchedTask.setErrorMessage(task.getErrorMessage());
        launchedTask.setRetryNum(task.getRetryNum());
        launchedTask.setLaunchTime(task.getLaunchTime());
        launchedTask.setRunningTime(task.getRunningTime());
        ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> taskLauncher =
                this.taskExecution.getExchangisLaunchManager().getTaskLauncher(DefaultTaskExecution.DEFAULT_LAUNCHER_NAME);
        try {
            AccessibleLauncherTask accessibleLauncherTask = taskLauncher.launcherTask(launchedTask);
            launchedTask.setLauncherTask(accessibleLauncherTask);
            this.taskExecution.getTaskManager().addRunningTask(launchedTask);
            // Add the launchedExchangisTask to the load balance poller
            List<LoadBalanceSchedulerTask<LaunchedExchangisTask>> loadBalanceSchedulerTasks =
                    this.taskExecution.getTaskSchedulerLoadBalancer().choose(launchedTask);
            Optional.ofNullable(loadBalanceSchedulerTasks).ifPresent(tasks -> tasks.forEach(loadBalanceSchedulerTask -> {
                loadBalanceSchedulerTask.getOrCreateLoadBalancePoller().push(launchedTask);
            }));
            return true;
        } catch (ExchangisTaskLaunchException e) {
            LOG.warn("Fail to subscribe launched task, reason: [{}]", e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean subscribe(LaunchedExchangisJobEntity job) {
        // Convert to launch-able job
        Calendar calendar = Calendar.getInstance();
        LaunchableExchangisJob launchableExchangisJob = new LaunchableExchangisJob();
        this.launchedJobDao.updateLaunchedJobDate(job.getJobExecutionId(), calendar.getTime());
        ExchangisJobVo jobVo = this.jobInfoService.getJob(job.getJobId(), false);
        if (Objects.nonNull(jobVo)){
            launchableExchangisJob.setExchangisJobInfo(new ExchangisJobInfo(jobVo));
            launchableExchangisJob.setName(jobVo.getJobName());
            launchableExchangisJob.setEngineType(jobVo.getEngineType());
            launchableExchangisJob.setCreateTime(calendar.getTime());
            launchableExchangisJob.setLastUpdateTime(calendar.getTime());
            launchableExchangisJob.setId(jobVo.getId());
            launchableExchangisJob.setExecUser(jobVo.getExecuteUser());
            launchableExchangisJob.setCreateUser(jobVo.getCreateUser());
            launchableExchangisJob.setJobExecutionId(job.getJobExecutionId());
            String tenancy = job.getExecuteUser();
            tenancy = org.apache.commons.lang.StringUtils.isNotBlank(tenancy)?
                    tenancy: TenancyParallelGroupFactory.DEFAULT_TENANCY;
            try {
                GenerationSchedulerTask schedulerTask = new GenerationSchedulerTask(taskGenerator, launchableExchangisJob);
                schedulerTask.setTenancy(tenancy);
                this.taskExecution.submit(schedulerTask);
                return true;
            } catch (Exception e) {
                LOG.warn("Fail to subscribe launched job, reason: [{}]", e.getMessage(), e);
            }
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unsubscribe(LaunchableExchangisTask task) {
        LOG.info("Unsubscribe launch task: [{}], id: [{}], execution_id: [{}], last_update_time: [{}]",
                task.getName(), task.getId(), task.getJobExecutionId(), task.getLastUpdateTime());
        this.launchedTaskDao.deleteLaunchedTask(task.getId() + "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delayToSubscribe(List<LaunchableExchangisTask> tasks) {
        for(LaunchableExchangisTask task : tasks){
            Calendar calendar = Calendar.getInstance();
            task.setLastUpdateTime(calendar.getTime());
            // Delay for each task
            int delayCount = Optional.ofNullable(task.getDelayCount()).orElse(0);
            calendar.add(Calendar.SECOND, launchDelayInSec[ delayCount % launchDelayInSec.length]);
            Date delayTime = calendar.getTime();
            task.setDelayTime(delayTime);
            task.setDelayCount(delayCount + 1);
        }
        this.launchableTaskDao.delayBatch(tasks);
    }

    public AbstractTaskExecution getTaskExecution() {
        return taskExecution;
    }

    public void setTaskExecution(AbstractTaskExecution taskExecution) {
        this.taskExecution = taskExecution;
    }
}
