package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
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
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.log.LogResult;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.DefaultTaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.vo.*;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.JOB_EXCEPTION_CODE;
import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR;

@Service
public class DefaultJobExecuteService implements JobExecuteService {
    private final static Logger LOG = LoggerFactory.getLogger(DefaultJobExecuteService.class);

    @Autowired
    private LaunchedTaskDao launchedTaskDao;

    @Autowired
    private LaunchedJobDao launchedJobDao;

    @Autowired
    private LaunchableTaskDao launchableTaskDao;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Task generator
     */
    @Resource
    private TaskGenerator<LaunchableExchangisJob> taskGenerator;

    /**
     * Task execution
     */
    @Resource
    private TaskExecution<LaunchableExchangisTask> taskExecution;

    @Autowired
    private JobLogService jobLogService;


    /**
     * Launch manager
     */
    @Resource
    private ExchangisTaskLaunchManager launchManager;

    @Override
    public List<ExchangisJobTaskVo> getExecutedJobTaskList(String jobExecutionId) {
        List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntities = launchedTaskDao.selectTaskListByJobExecutionId(jobExecutionId);
        List<ExchangisJobTaskVo> jobTaskList = new ArrayList<>();
        if(launchedExchangisTaskEntities != null) {
            try {
                launchedExchangisTaskEntities.forEach(taskEntity -> {
                            ExchangisJobTaskVo exchangisTaskVO = modelMapper.map(taskEntity, ExchangisJobTaskVo.class);
                            jobTaskList.add(exchangisTaskVO);
                        }
                );
            } catch (Exception e) {
                LOG.error("Exception happened while get TaskLists mapping to Vo(获取task列表映射至页面是出错，请校验任务信息), " + "message: " + e.getMessage(), e);
            }
        }

        return jobTaskList;
    }

    @Override
    public ExchangisJobProgressVo getExecutedJobProgressInfo(String jobExecutionId) throws ExchangisJobServerException {
        LaunchedExchangisJobEntity launchedExchangisJobEntity = launchedJobDao.searchLaunchedJob(jobExecutionId);
        if (launchedExchangisJobEntity == null) {
            throw new ExchangisJobServerException(31100, "Get jobProgress information is null(獲取job进度信息为空), " + "jobExecutionId = " + jobExecutionId);
        }
        ExchangisJobProgressVo jobProgressVo = null;
        try {
            jobProgressVo = modelMapper.map(launchedExchangisJobEntity, ExchangisJobProgressVo.class);
            List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntity = launchedTaskDao.selectTaskListByJobExecutionId(jobExecutionId);
            ExchangisJobProgressVo finalJobProgressVo = jobProgressVo;
            launchedExchangisTaskEntity.forEach(taskEntity -> {
                finalJobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo(taskEntity.getTaskId(), taskEntity.getName(), taskEntity.getStatus(), taskEntity.getProgress()));
                //jobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo(taskEntity.getTaskId(), taskEntity.getName(), taskEntity.getStatus(), taskEntity.getProgress()));
            });
        } catch (Exception e){
            LOG.error("Get job and task progress happen execption ," +  "[jobExecutionId =" + jobExecutionId + "]", e);
        }
        return jobProgressVo;
    }

    @Override
    public ExchangisJobProgressVo getJobStatus(String jobExecutionId) {
        LaunchedExchangisJobEntity launchedExchangisJobEntity = launchedJobDao.searchLaunchedJob(jobExecutionId);
        ExchangisJobProgressVo jobProgressVo = null;
        try {
            jobProgressVo = modelMapper.map(launchedExchangisJobEntity, ExchangisJobProgressVo.class);
        } catch (Exception e) {
            LOG.error("Get job status happen execption, " +  "[jobExecutionId =" + jobExecutionId + "]（获取作业状态错误）", e);
        }
        return jobProgressVo;
    }

    @Override
    public void killJob(String jobExecutionId) {
        Calendar calendar = Calendar.getInstance();
        launchedJobDao.upgradeLaunchedJobStatus(jobExecutionId, TaskStatus.Cancelled.name(), calendar.getTime());
    }

    @Override
    public ExchangisLaunchedTaskMetricsVO getLaunchedTaskMetrics(String taskId, String jobExecutionId) throws ExchangisJobServerException {
        LaunchedExchangisTaskEntity launchedExchangisTaskEntity = launchedTaskDao.getLaunchedTaskMetrics(jobExecutionId, taskId);
        if (launchedExchangisTaskEntity == null) {
            throw new ExchangisJobServerException(31100, "Get task metrics happened Exception (獲取task指标信息为空), " + "jobExecutionId = " + jobExecutionId+ "taskId = " + taskId);
        }
        ExchangisLaunchedTaskMetricsVO exchangisLaunchedTaskVo = new ExchangisLaunchedTaskMetricsVO();
        exchangisLaunchedTaskVo.setTaskId(launchedExchangisTaskEntity.getTaskId());
        exchangisLaunchedTaskVo.setName(launchedExchangisTaskEntity.getName());
        exchangisLaunchedTaskVo.setStatus(launchedExchangisTaskEntity.getStatus().name());
        //exchangisLaunchedTaskVo.setMetrics(launchedExchangisTaskEntity.getMetricsMap());

        return exchangisLaunchedTaskVo;
    }

    @Override
    public boolean hasExecuteJobAuthority(String jobExecutionId, String userName) {
        return hasExecuteJobAuthority(this.launchedJobDao.searchLaunchedJob(jobExecutionId) , userName);
    }

    /**
     * Check if has the authority of accessing execution job
     * @param launchedExchangisJob launched job
     * @param userName userName
     * @return
     */
    public boolean hasExecuteJobAuthority(LaunchedExchangisJobEntity launchedExchangisJob, String userName){
//        return Objects.nonNull(launchedExchangisJob) && launchedExchangisJob.getExecuteUser().equals(userName);
        return true;
    }

    @Override
    public ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, LogQuery logQuery, String userName) throws ExchangisJobServerException {
        LaunchedExchangisJobEntity launchedExchangisJob = this.launchedJobDao.searchLogPathInfo(jobExecutionId);
        if (Objects.isNull(launchedExchangisJob) || !hasExecuteJobAuthority(launchedExchangisJob, userName)){
            throw new ExchangisJobServerException(LOG_OP_ERROR.getCode(), "Unable to find the launched job by [" + jobExecutionId + "]", null);
        }
        LogResult logResult = jobLogService.logsFromPageAndPath(launchedExchangisJob.getLogPath(), logQuery);
        return resultToCategoryLog(logResult, launchedExchangisJob.getStatus());
    }

    @Override
    public ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, LogQuery logQuery, String userName)
            throws ExchangisJobServerException, ExchangisTaskLaunchException {
        LaunchedExchangisTaskEntity launchedTaskEntity = this.launchedTaskDao.getLaunchedTaskEntity(taskId);
        if (Objects.isNull(launchedTaskEntity)){
            return resultToCategoryLog(new LogResult(0, false, new ArrayList<>()), TaskStatus.Inited);
        }
        if (StringUtils.isBlank(launchedTaskEntity.getLinkisJobId())){
            TaskStatus status = launchedTaskEntity.getStatus();
            // Means that the task is not ready or task submit failed
            return resultToCategoryLog(new LogResult(0, TaskStatus.isCompleted(status), new ArrayList<>()), status);
        }
        if (!hasExecuteJobAuthority(jobExecutionId, userName)){
            throw new ExchangisJobServerException(LOG_OP_ERROR.getCode(), "Not have permission of accessing task [" + taskId + "]", null);
        }
        // Construct the launchedExchangisTask
        LaunchedExchangisTask launchedTask = new LaunchedExchangisTask();
        launchedTask.setLinkisJobId(launchedTaskEntity.getLinkisJobId());
        launchedTask.setLinkisJobInfo(launchedTaskEntity.getLinkisJobInfo());
        launchedTask.setTaskId(launchedTaskEntity.getTaskId());
        launchedTask.setExecuteUser(launchedTaskEntity.getExecuteUser());
        launchedTask.setEngineType(launchedTaskEntity.getEngineType());
        ExchangisTaskLauncher<LaunchableExchangisTask, LaunchedExchangisTask> taskLauncher =
                this.launchManager.getTaskLauncher(DefaultTaskExecution.DEFAULT_LAUNCHER_NAME);
        if (Objects.isNull(taskLauncher)){
            throw new ExchangisJobServerException(LOG_OP_ERROR.getCode(), "Unable to find the suitable launcher for [task: " + taskId + ", engine type: " +
                    launchedTask.getEngineType() +"]", null);
        }
        AccessibleLauncherTask accessibleLauncherTask = taskLauncher.launcherTask(launchedTask);
        return resultToCategoryLog(accessibleLauncherTask.queryLogs(logQuery), launchedTaskEntity.getStatus());
    }

    @Override
    public List<ExchangisLaunchedJobListVO> getExecutedJobList(Long jobId, String jobName, String status,
                                                               Long launchStartTime, Long launchEndTime, Integer  current, Integer size) {
        List<ExchangisLaunchedJobListVO> jobList = new ArrayList<>();
        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);
        List<LaunchedExchangisJobEntity> jobEntitylist = launchedJobDao.getAllLaunchedJob(jobId, jobName, status, startTime, endTime);
        if(jobEntitylist != null) {
            try {
                for (int i = 0; i < jobEntitylist.size(); i++) {
                    ExchangisLaunchedJobListVO exchangisJobVo = modelMapper.map(jobEntitylist.get(i), ExchangisLaunchedJobListVO.class);
                    if(jobEntitylist.get(i).getExchangisJobEntity() == null || jobEntitylist.get(i).getExchangisJobEntity().getSource() == null) {
                        exchangisJobVo.setExecuteNode("-");
                    }
                    else {
                        Map<String, Object> sourceObject = Json.fromJson(jobEntitylist.get(i).getExchangisJobEntity().getSource(), Map.class);
                        exchangisJobVo.setExecuteNode(sourceObject.get("execute_node").toString());
                    }
                    List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntities = launchedTaskDao.selectTaskListByJobExecutionId(jobEntitylist.get(i).getJobExecutionId());
                    if(launchedExchangisTaskEntities == null){
                        exchangisJobVo.setFlow((long) 0);
                    }
                    else {
                        int flows = 0;
                        int taskNum = launchedExchangisTaskEntities.size();
                        for (int j = 0; j < taskNum; j++) {
                            if(launchedExchangisTaskEntities.get(j).getMetricsMap() == null){
                                flows += 0;
                                continue;
                            }
                            Map<String, Object> flowMap = Json.fromJson(launchedExchangisTaskEntities.get(j).getMetricsMap().get("traffic").toString(), Map.class);
                            flows += flowMap == null ? 0 : Integer.parseInt(flowMap.get("flow").toString());
                        }
                        exchangisJobVo.setFlow(taskNum == 0 ? 0 : (long) (flows / taskNum));
                    }
                    jobList.add(exchangisJobVo);
                }
            } catch (Exception e) {
                LOG.error("Exception happened while get JobLists mapping to Vo(获取job列表映射至页面是出错，请校验任务信息), " + "message: " + e.getMessage(), e);
            }
        }
        return jobList;
    }

    @Override
    public int count(Long jobId, String jobName, String status, Long launchStartTime, Long launchEndTime) {
        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);
        return 100;
    }

    @Override
    public String executeJob(ExchangisJobInfo jobInfo, String execUser) throws ExchangisJobServerException {
        // Build generator scheduler task
        GenerationSchedulerTask schedulerTask = null;
        try {
            schedulerTask = new GenerationSchedulerTask(taskGenerator, jobInfo);
        } catch (ExchangisTaskGenerateException e) {
            throw new ExchangisJobServerException(JOB_EXCEPTION_CODE.getCode(), "Exception in initial the launchable job", e);
        }
        // The scheduler task id is execution id
        String jobExecutionId = schedulerTask.getId();
        // Use exec user as tenancy
        schedulerTask.setTenancy(execUser);
        LOG.info("Submit the generation scheduler task: [{}] for job: [{}], tenancy: [{}] to TaskExecution", jobExecutionId, jobInfo.getId(), execUser);
        try {
            taskExecution.submit(schedulerTask);
        } catch (ExchangisSchedulerException e) {
            throw new ExchangisJobServerException(JOB_EXCEPTION_CODE.getCode(), "Exception in submitting to taskExecution", e);
        }
        return jobExecutionId;
    }

    /**
     * Convert the log result to category log
     * @param logResult log result
     * @param status status
     * @return category log
     */
    private ExchangisCategoryLogVo resultToCategoryLog(LogResult logResult, TaskStatus status){
        ExchangisCategoryLogVo categoryLogVo = new ExchangisCategoryLogVo();
        boolean noLogs = logResult.getLogs().isEmpty();
        // TODO Cannot find the log
        if (noLogs){
//            logResult.getLogs().add("<<The log content is empty>>");
            if (TaskStatus.isCompleted(status)){
                logResult.setEnd(true);
//                categoryLogVo.setIsEnd(true);
            }
        }
        categoryLogVo.newCategory("error", log -> log.contains("ERROR") || noLogs);
        categoryLogVo.newCategory("warn", log -> log.contains("WARN") || noLogs);
        categoryLogVo.newCategory("info", log -> log.contains("INFO") || noLogs);
        categoryLogVo.processLogResult(logResult, false);
        if (!noLogs) {
            categoryLogVo.getLogs().put("all", StringUtils.join(logResult.getLogs(), "\n"));
        }
        return categoryLogVo;
    }

   /* public static void main(String[] args) {
        ExchangisJobParamsContent.ExchangisJobParamsItem jobParamsContent = new ExchangisJobParamsContent.ExchangisJobParamsItem();
        jobParamsContent = Json.fromJson("", jobParamsContent.getClass());
        System.out.println(jobParamsContent.getConfigValue());
    }*/
}
