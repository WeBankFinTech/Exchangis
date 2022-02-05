package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.google.common.base.Joiner;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisTaskLaunchManager;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisJob;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
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
import org.apache.linkis.server.Message;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.JOB_EXCEPTION_CODE;

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
        launchedJobDao.upgradeLaunchedJobStatus(jobExecutionId, "Cancelled", calendar.getTime());
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
        //exchangisLaunchedTaskVo.setStatus(launchedExchangisTaskEntity.getStatus().name());
        exchangisLaunchedTaskVo.setMetrics(launchedExchangisTaskEntity.getMetricsMap());

        return exchangisLaunchedTaskVo;
    }

    @Override
    public ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords,
                                                String onlyKeywords, Integer lastRows) throws ExchangisJobServerException {
        LaunchedExchangisJobEntity launchedExchangisJob = this.launchedJobDao.searchLogPathInfo(jobExecutionId);
        if (Objects.isNull(launchedExchangisJob)){
            throw new ExchangisJobServerException(LOG_OP_ERROR.getCode(), "Unable to find the launched job by [" + jobExecutionId + "]", null);
        }
        LogResult logResult = jobLogService.logsFromPageAndPath(launchedExchangisJob.getLogPath(), new LogQuery(fromLine, pageSize,
                ignoreKeywords, onlyKeywords, lastRows));
        ExchangisCategoryLogVo categoryLogVo = new ExchangisCategoryLogVo();
        // TODO Cannot find the log
        if (logResult.getLogs().isEmpty()){
            logResult.getLogs().add("The log content is not ready");
        }
        if (logResult.getLogs().isEmpty()){
            categoryLogVo.setEndLine(logResult.getEndLine());
            categoryLogVo.setIsEnd(logResult.isEnd());
            if (TaskStatus.isCompleted(launchedExchangisJob.getStatus())){
                categoryLogVo.setIsEnd(true);
            }
        }else {
            categoryLogVo.newCategory("error", log -> log.contains("ERROR"));
            categoryLogVo.newCategory("warn", log -> log.contains("WARN"));
            categoryLogVo.newCategory("info", log -> log.contains("INFO"));
            categoryLogVo.processLogResult(logResult);
            categoryLogVo.getLogs().put("all", StringUtils.join(logResult.getLogs(), "\n"));
        }
        return categoryLogVo;
    }

    @Override
    public ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows) throws ExchangisTaskLaunchException {
        LaunchableExchangisTask launchableExchangisTask = launchableTaskDao.getLaunchableTask(taskId);
        //LaunchedExchangisTaskEntity launchedExchangisTaskEntity = launchedTaskDao.getLaunchedTaskMetrics(jobExecutionId, taskId);
        LaunchedExchangisTask launchedExchangisTask = new LaunchedExchangisTask(launchableExchangisTask);
        AccessibleLauncherTask task = launchManager.getTaskLauncher(DefaultTaskExecution.DEFAULT_LAUNCHER_NAME)
                .launcherTask(launchedExchangisTask);
        TaskLogQuery logQuery = new TaskLogQuery(fromLine, pageSize);
        if(ignoreKeywords != null){
            logQuery.setIgnoreKeywords(ignoreKeywords);
        }
        if(onlyKeywords != null){
            logQuery.setOnlyKeywords(onlyKeywords);
        }
        if(lastRows != null){
            logQuery.setLastRows(lastRows);
        }

        TaskLog taskLog = null;
        try {
            taskLog = task.queryLogs(logQuery);
        } catch (Throwable e) {
            throw new ExchangisTaskLaunchException( "Get task logs happened Exception (獲取task日志时出错), " + "jobExecutionId = " + jobExecutionId+ "taskId = " + taskId, e);
        }
        List<String> taskLogs = taskLog.getLogs();

    public ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows) {
        int from = 1;
        int size = 10;
        if (Objects.nonNull(fromLine) && fromLine > 0) {
            from = fromLine;
        }

        if (Objects.nonNull(pageSize) && pageSize > 0) {
            size = pageSize;
        }

        // TODO mock data
        int warningLineIdx = 1;
        //LinkedList<String> taskLogs = getMockLogs();
        int mockLogsSize = taskLogs.size();
        LinkedList<String> errLogs = new LinkedList<>();
        LinkedList<String> warningLogs = new LinkedList<>();
        LinkedList<String> infoLogs = new LinkedList<>();
        LinkedList<String> allLogs = new LinkedList<>();
        int endLine = from;
//        int actualSize = 0;
        if (Objects.nonNull(lastRows) && lastRows > 0) {
            for (int i = mockLogsSize - lastRows; i <= mockLogsSize; i++){
                allLogs.add(taskLogs.get(i));
                if (taskLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(taskLogs.get(i - 1));
                }
                else if (taskLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(taskLogs.get(i - 1));
                }
                else if (taskLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(taskLogs.get(i - 1));
                }
                endLine = mockLogsSize;
            }
        }
        else {
            for (int i = from, j = 0; (i <= mockLogsSize && j < size); i++, j++) {
                allLogs.add(taskLogs.get(i - 1));

                if (taskLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(taskLogs.get(i - 1));
                } else if (taskLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(taskLogs.get(i - 1));
                } else if (taskLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(taskLogs.get(i - 1));
                }
                endLine = i;
            }
        }

        String allLogsStr = Joiner.on("\n").join(allLogs);
        String warningLogsStr = Joiner.on("\n").join(warningLogs);
        String infoLogsStr = Joiner.on("\n").join(infoLogs);
        String errLogsStr = Joiner.on("\n").join(errLogs);
        Map<String, String> logs = new HashMap<>();
        logs.put("all", allLogsStr);
        logs.put("error", errLogsStr);
        logs.put("warn", warningLogsStr);
        logs.put("info", infoLogsStr);

        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/task/execution/{taskId}/log");
        message.data("endLine", endLine);
        message.data("isEnd", from + size >= mockLogsSize);
        message.data("logs", logs);
        return null;
    }

    @Override
    public List<ExchangisLaunchedJobListVO> getExecutedJobList(Long jobId, String jobName, String status,
                                                               Long launchStartTime, Long launchEndTime, Integer  current, Integer size) {
        List<ExchangisLaunchedJobListVO> jobList = new ArrayList<>();
        List<LaunchedExchangisJobEntity> jobEntitylist = launchedJobDao.getAllLaunchedJob(jobId, jobName, status, launchStartTime, launchEndTime);
        if(jobEntitylist != null) {
            try {
                jobEntitylist.forEach(jobEntity -> {
                    ExchangisLaunchedJobListVO exchangisJobVo = modelMapper.map(jobEntity, ExchangisLaunchedJobListVO.class);
                    Map<String, Object> sourceObject = Json.fromJson(jobEntity.getExchangisJobEntity().getSource(), Map.class);
                    exchangisJobVo.setExecuteNode(sourceObject.get("execute_node").toString());
                    List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntities = launchedTaskDao.selectTaskListByJobExecutionId(jobEntity.getJobExecutionId());
                    if(launchedExchangisTaskEntities == null){
                        exchangisJobVo.setFlow((long) 0);
                    }
                    else {
                        int flows = 0;
                        int taskNum = launchedExchangisTaskEntities.size();
                        for (int i = 0; i < taskNum; i++) {
                            Map<String, Object> flowMap = Json.fromJson(launchedExchangisTaskEntities.get(i).getMetricsMap().get("traffic").toString(), Map.class);
                            flows += flowMap == null ? 0 : Integer.parseInt(flowMap.get("flow").toString());
                        }
                        exchangisJobVo.setFlow((long) (flows / taskNum));
                    }
                    jobList.add(exchangisJobVo);
                        }
                );
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


    // TODO
    private LinkedList<String> getMockLogs() {
        LinkedList<String> logs = new LinkedList<>();
        logs.add("2021-12-27 15:41:08.041 INFO Variables substitution ended successfully");
        logs.add("2021-12-27 15:41:08.041 WARN You submitted a sql without limit, DSS will add limit 5000 to your sql");
        logs.add("2021-12-27 15:41:08.041 INFO SQL code check has passed");
        logs.add("job is scheduled.");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is Scheduled. Please wait it to run.");
        logs.add("Your job is being scheduled by orchestrator.");
        logs.add("Job with jobId : IDE_hdfs_spark_1 and execID : IDE_hdfs_spark_1 submitted");
        logs.add("2021-12-27 15:41:08.041 INFO You have submitted a new job, script code (after variable substitution) is");
        logs.add("************************************SCRIPT CODE************************************");
        logs.add("select * from linkis_db.gujiantestdb1 limit 5000;");
        // --- 10

        logs.add("************************************SCRIPT CODE************************************");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is accepted,  jobID is IDE_hdfs_spark_1 and taskID is 771 in ServiceInstance(linkis-cg-entrance, ecs-f0cf-0004:9104). Please wait it to be scheduled");
        logs.add("2021-12-27 15:41:08.041 INFO job is running.");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is Running now. Please wait it to complete.");
        logs.add("Job with jobGroupId : 771 and subJobId : 757 was submitted to Orchestrator.");
        logs.add("2021-12-27 15:41:08.041 INFO Background is starting a new engine for you, it may take several seconds, please wait");
        logs.add("2021-12-27 15:41:08.041 INFO EngineConn local log path: ServiceInstance(linkis-cg-engineconn, ecs-f0cf-0004:46760) /opt/appcom/tmp/hdfs/workDir/cc7fbf2c-b72e-4124-b872-8c81801c822c/logs");
        logs.add("2021-12-27 15:41:08.041 INFO yarn application id: application_1622705945711_0118");
        logs.add("ecs-f0cf-0004:46760 >> select * from linkis_db.gujiantestdb1 limit 5000");
        logs.add("ecs-f0cf-0004:46760 >> Time taken: 382, Fetched 1 row(s).");
        // --- 10

        logs.add("************************************SCRIPT CODE************************************");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is accepted,  jobID is IDE_hdfs_spark_1 and taskID is 771 in ServiceInstance(linkis-cg-entrance, ecs-f0cf-0004:9104). Please wait it to be scheduled");
        logs.add("2021-12-27 15:41:08.041 INFO job is running.");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is Running now. Please wait it to complete.");
        logs.add("Job with jobGroupId : 771 and subJobId : 757 was submitted to Orchestrator.");
        logs.add("2021-12-27 15:41:08.041 INFO Background is starting a new engine for you, it may take several seconds, please wait");
        logs.add("2021-12-27 15:41:08.041 INFO EngineConn local log path: ServiceInstance(linkis-cg-engineconn, ecs-f0cf-0004:46760) /opt/appcom/tmp/hdfs/workDir/cc7fbf2c-b72e-4124-b872-8c81801c822c/logs");
        logs.add("2021-12-27 15:41:08.041 INFO yarn application id: application_1622705945711_0118");
        logs.add("ecs-f0cf-0004:46760 >> select * from linkis_db.gujiantestdb1 limit 5000");
        logs.add("ecs-f0cf-0004:46760 >> Time taken: 382, Fetched 1 row(s).");
        // --- 10

        logs.add("************************************SCRIPT CODE************************************");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is accepted,  jobID is IDE_hdfs_spark_1 and taskID is 771 in ServiceInstance(linkis-cg-entrance, ecs-f0cf-0004:9104). Please wait it to be scheduled");
        logs.add("2021-12-27 15:41:08.041 INFO job is running.");
        logs.add("2021-12-27 15:41:08.041 INFO Your job is Running now. Please wait it to complete.");
        logs.add("Job with jobGroupId : 771 and subJobId : 757 was submitted to Orchestrator.");
        logs.add("2021-12-27 15:41:08.041 INFO Background is starting a new engine for you, it may take several seconds, please wait");
        logs.add("2021-12-27 15:41:08.041 INFO EngineConn local log path: ServiceInstance(linkis-cg-engineconn, ecs-f0cf-0004:46760) /opt/appcom/tmp/hdfs/workDir/cc7fbf2c-b72e-4124-b872-8c81801c822c/logs");
        logs.add("2021-12-27 15:41:08.041 INFO yarn application id: application_1622705945711_0118");
        logs.add("ecs-f0cf-0004:46760 >> select * from linkis_db.gujiantestdb1 limit 5000");
        logs.add("ecs-f0cf-0004:46760 >> Time taken: 382, Fetched 1 row(s).");
        // --- 10

        logs.add("Your subjob : 757 execue with state succeed, has 1 resultsets.");
        logs.add("Congratuaions! Your job : IDE_hdfs_spark_1 executed with status succeed and 0 results.");
        logs.add("2021-12-27 15:41:09.041 INFO job is completed.");
        logs.add("2021-12-27 15:41:09.041 INFO Task creation time(任务创建时间): 2021-12-27 15:41:08, Task scheduling time(任务调度时间): 2021-12-27 15:41:08, Task start time(任务开始时间): 2021-12-27 15:41:08, Mission end time(任务结束时间): 2021-12-27 15:41:09");
        logs.add("2021-12-27 15:41:09.041 INFO Your mission(您的任务) 771 The total time spent is(总耗时时间为): 880 ms");
        logs.add("2021-12-27 15:41:09.041 INFO Congratulations. Your job completed with status Success.");
        logs.add("**result tips: 任务执行完成，正在获取结果集！");
        logs.add("**result tips: 获取结果集成功！");
        // --- 8
        return logs;
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
}
