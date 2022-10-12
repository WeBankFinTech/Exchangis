package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
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
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchableTaskDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskGenerateException;
import com.webank.wedatasphere.exchangis.job.server.execution.DefaultTaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.TaskExecution;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.TaskGenerator;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks.GenerationSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.metrics.ExchangisMetricsVo;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.MetricConverterFactory;
import com.webank.wedatasphere.exchangis.job.server.metrics.converter.MetricsConverter;
import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.vo.*;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.*;

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

    /**
     * Metrics converter factory
     */
    @Resource
    private MetricConverterFactory<ExchangisMetricsVo> metricConverterFactory;

    @Override
    public List<ExchangisJobTaskVo> getExecutedJobTaskList(String jobExecutionId) throws ExchangisJobServerException{
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
            });
        } catch (Exception e){
            LOG.error("Get job and task progress happen execption ," +  "[jobExecutionId =" + jobExecutionId + "]", e);
        }
        return jobProgressVo;
    }

    @Override
    public ExchangisJobProgressVo getJobStatus(String jobExecutionId) throws ExchangisJobServerException{
        LaunchedExchangisJobEntity launchedExchangisJobEntity = launchedJobDao.searchLaunchedJob(jobExecutionId);
        ExchangisJobProgressVo jobProgressVo = null;
        try {
            jobProgressVo = modelMapper.map(launchedExchangisJobEntity, ExchangisJobProgressVo.class);
        } catch (Exception e) {
            LOG.error("Get job status happen execption, " +  "[jobExecutionId =" + jobExecutionId + "]（获取作业状态错误）", e);
        }

        boolean allTaskStatus = false;

        assert jobProgressVo != null;
        if(TaskStatus.isCompleted(jobProgressVo.getStatus())) {
            List<String> taskStatusList = launchedTaskDao.getTaskStatusList(jobExecutionId);
            allTaskStatus = taskStatusList.isEmpty();
            if (!allTaskStatus){
                allTaskStatus = taskStatusList.stream().allMatch( status ->
                        StringUtils.isNotBlank(status) && TaskStatus.isCompleted(TaskStatus.valueOf(status)));
            }
        }
        jobProgressVo.setAllTaskStatus(allTaskStatus);
        return jobProgressVo;
    }

    @Override
    public void killJob(String jobExecutionId) throws ExchangisJobServerException{
        Calendar calendar = Calendar.getInstance();
        launchedJobDao.upgradeLaunchedJobStatus(jobExecutionId, TaskStatus.Cancelled.name(), calendar.getTime());
    }

    @Override
    public ExchangisLaunchedTaskMetricsVo getLaunchedTaskMetrics(String taskId, String jobExecutionId) throws ExchangisJobServerException {
        LaunchedExchangisTaskEntity launchedExchangisTaskEntity = launchedTaskDao.getLaunchedTaskMetrics(jobExecutionId, taskId);
        ExchangisLaunchedTaskMetricsVo exchangisLaunchedTaskVo = new ExchangisLaunchedTaskMetricsVo();
        exchangisLaunchedTaskVo.setTaskId(launchedExchangisTaskEntity.getTaskId());
        exchangisLaunchedTaskVo.setName(launchedExchangisTaskEntity.getName());
        exchangisLaunchedTaskVo.setStatus(launchedExchangisTaskEntity.getStatus().name());
        MetricsConverter<ExchangisMetricsVo> metricsConverter = metricConverterFactory.getOrCreateMetricsConverter(launchedExchangisTaskEntity.getEngineType());
        if (Objects.nonNull(metricsConverter)){
            try {
                exchangisLaunchedTaskVo.setMetrics(metricsConverter.convert(launchedExchangisTaskEntity.getMetricsMap()));
            }catch (ExchangisJobServerException e){
                // Print the problem in convert metrics vo
                LOG.warn("Problem occurred in convert of metrics vo", e);
            }
        }
        return exchangisLaunchedTaskVo;
    }

    @Override
    public ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, LogQuery logQuery) throws ExchangisJobServerException {
        LaunchedExchangisJobEntity launchedExchangisJob = this.launchedJobDao.searchLogPathInfo(jobExecutionId);
        LogResult logResult = jobLogService.logsFromPageAndPath(launchedExchangisJob.getLogPath(), logQuery);
        return resultToCategoryLog(logQuery, logResult, launchedExchangisJob.getStatus());
    }

    @Override
    public ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, LogQuery logQuery)
            throws ExchangisJobServerException, ExchangisTaskLaunchException {
        LaunchedExchangisTaskEntity launchedTaskEntity = this.launchedTaskDao.getLaunchedTaskEntity(taskId);
        if (Objects.isNull(launchedTaskEntity)){
            return resultToCategoryLog(logQuery, new LogResult(0, false, new ArrayList<>()), TaskStatus.Inited);
        }
        if (StringUtils.isBlank(launchedTaskEntity.getLinkisJobId())){
            TaskStatus status = launchedTaskEntity.getStatus();
            // Means that the task is not ready or task submit failed
            return resultToCategoryLog(logQuery, new LogResult(0, TaskStatus.isCompleted(status), new ArrayList<>()), status);
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
        return resultToCategoryLog(logQuery, accessibleLauncherTask.queryLogs(logQuery), launchedTaskEntity.getStatus());
    }

    @Override
    public List<ExchangisLaunchedJobListVo> getExecutedJobList(String jobExecutionId, String jobName, String status,
                                                               Long launchStartTime, Long launchEndTime, int current, int size, HttpServletRequest request) throws ExchangisJobServerException{
        if (current <= 0) {
            current = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        int start = (current - 1) * size;
        List<ExchangisLaunchedJobListVo> jobList = new ArrayList<>();
        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);
        List<LaunchedExchangisJobEntity> jobEntitylist = launchedJobDao.getAllLaunchedJob(jobExecutionId, jobName, status, startTime, endTime, start, size, UserUtils.getLoginUser(request));
        //LOG.info("ExecutedJobList information: " + jobExecutionId + jobName + status + launchStartTime + launchEndTime + current + size);
        if(jobEntitylist != null) {
            try {
                for (LaunchedExchangisJobEntity launchedExchangisJobEntity : jobEntitylist) {
                    ExchangisLaunchedJobListVo exchangisJobVo = modelMapper.map(launchedExchangisJobEntity, ExchangisLaunchedJobListVo.class);
                    if (launchedExchangisJobEntity.getExchangisJobEntity() == null || launchedExchangisJobEntity.getExchangisJobEntity().getSource() == null) {
                        exchangisJobVo.setExecuteNode("-");
                    } else {
                        Map<String, Object> sourceObject = Json.fromJson(launchedExchangisJobEntity.getExchangisJobEntity().getSource(), Map.class);
                        if (Objects.nonNull(sourceObject)){
                            exchangisJobVo.setExecuteNode(String.valueOf(sourceObject
                                    .getOrDefault("executeNode", "-")));
                        }
                    }
                    List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntities = launchedTaskDao.selectTaskListByJobExecutionId(launchedExchangisJobEntity.getJobExecutionId());
                    if (launchedExchangisTaskEntities == null) {
                        exchangisJobVo.setFlow((long) 0);
                    } else {
                        double flows = 0;
                        int taskNum = launchedExchangisTaskEntities.size();
                        for (LaunchedExchangisTaskEntity launchedExchangisTaskEntity : launchedExchangisTaskEntities) {
                            MetricsConverter<ExchangisMetricsVo> metricsConverter = metricConverterFactory.getOrCreateMetricsConverter(launchedExchangisTaskEntity.getEngineType());
                            ExchangisLaunchedTaskMetricsVo exchangisLaunchedTaskVo = new ExchangisLaunchedTaskMetricsVo();
                            if (launchedExchangisTaskEntity.getMetricsMap() == null) {
                                flows += 0;
                                continue;
                            }
                            exchangisLaunchedTaskVo.setMetrics(metricsConverter.convert(launchedExchangisTaskEntity.getMetricsMap()));
                            Map<String, Object> flowMap = (Map<String, Object>) launchedExchangisTaskEntity.getMetricsMap().get("traffic");
                            //Map<String, Object> flowMap = (Map<String, Object>) launchedExchangisTaskEntity.getMetricsMap().get("traffic");
                            //flows += flowMap == null ? 0 : Integer.parseInt(flowMap.get("flow").toString());
                            flows += exchangisLaunchedTaskVo.getMetrics().getTraffic().getFlow();
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
    public int count(String jobExecutionId, String jobName, String status, Long launchStartTime, Long launchEndTime, HttpServletRequest request) {
        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);

        return launchedJobDao.count(jobExecutionId, jobName, status, startTime, endTime, UserUtils.getLoginUser(request));
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
    private ExchangisCategoryLogVo resultToCategoryLog(LogQuery logQuery, LogResult logResult, TaskStatus status){
        ExchangisCategoryLogVo categoryLogVo = new ExchangisCategoryLogVo();
        boolean noLogs = logResult.getLogs().isEmpty();
        for (int i = 0; i < logResult.getLogs().size(); i++) {
            if (logResult.getLogs().get(i).contains("password")) {
                LOG.info("Sensitive information in there: {}", logResult.getLogs().get(i));
                logResult.getLogs().set(i, "----");
                LOG.info("Change line is: {}", logResult.getLogs().get(i));
            }
        }
        if (Objects.nonNull(logQuery.getLastRows())){
            logResult.setEnd(true);
        }else if (noLogs){
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

    @Transactional
    @Override
    public void deleteJob(String jobExecutionId) throws ExchangisJobServerException {
        List<String> taskStatusList = launchedTaskDao.getTaskStatusList(jobExecutionId);
        if(taskStatusList.contains("Inited") || taskStatusList.contains("Scheduled") || taskStatusList.contains("Running") || taskStatusList.contains("WaitForRetry")){
            throw new ExchangisJobServerException(JOB_EXCEPTION_CODE.getCode(), "不能删除该作业");
        }else {
            launchedTaskDao.deleteTask(jobExecutionId);
            launchedJobDao.deleteJob(jobExecutionId);
        }
    }

    @Override
    public List<String> allTaskStatus(String jobExecutionId) throws ExchangisJobServerException {
        List<String> taskStatusList = launchedTaskDao.getTaskStatusList(jobExecutionId);
        return taskStatusList;
    }

}
