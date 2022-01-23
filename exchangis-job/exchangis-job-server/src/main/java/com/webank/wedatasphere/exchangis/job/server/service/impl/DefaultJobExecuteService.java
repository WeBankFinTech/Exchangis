package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.google.common.base.Joiner;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedJobDao;
import com.webank.wedatasphere.exchangis.job.server.dao.LaunchedTaskDao;
import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.*;
import org.apache.linkis.server.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultJobExecuteService implements JobExecuteService {

    @Autowired
    private ExchangisJobService exchangisJobService;

    @Autowired
    private LaunchedTaskDao launchedTaskDao;

    @Autowired
    private LaunchedJobDao launchedJobDao;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ExchangisJobTaskVo> getExecutedJobTaskList(String jobExecutionId) {
        List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntities = launchedTaskDao.selectTaskListByJobExecutionId(jobExecutionId);
        List<ExchangisJobTaskVo> jobTaskList = new ArrayList<>();
        launchedExchangisTaskEntities.forEach(taskEntity -> {
                    ExchangisJobTaskVo exchangisTaskVO = modelMapper.map(taskEntity, ExchangisJobTaskVo.class);
                    jobTaskList.add(exchangisTaskVO);
                }
        );
        /*List<ExchangisJobTaskVo> jobTaskList = new ArrayList<>();
        Date date = new Date();
        //LaunchedExchangisTaskEntity launchedExchangisTaskEntity = new LaunchedExchangisTaskEntity();
        ExchangisJobTaskVo launchedTaskEntity1 = new ExchangisJobTaskVo((long) 5, "test-1", "Inited",
                date, date, date, "sqoop", null, null, "enjoyyin");
        ExchangisJobTaskVo launchedTaskEntity2 = new ExchangisJobTaskVo((long) 7, "test-7", "Running",
                date, date, date, "datax", null, null, "tikazhang");

        jobTaskList.add(launchedTaskEntity1);

        jobTaskList.add(launchedTaskEntity2);*/

        return jobTaskList;
    }

    @Override
    public ExchangisJobProgressVo getExecutedJobProgressInfo(String jobExecutionId) {
        LaunchedExchangisJobEntity launchedExchangisJobEntity = launchedJobDao.searchLaunchedJob(jobExecutionId);
        ExchangisJobProgressVo jobProgressVo = modelMapper.map(launchedExchangisJobEntity, ExchangisJobProgressVo.class);
        List<LaunchedExchangisTaskEntity> launchedExchangisTaskEntity = launchedTaskDao.selectTaskListByJobExecutionId(jobExecutionId);
        launchedExchangisTaskEntity.forEach(taskEntity -> {
            jobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo(taskEntity.getTaskId(), taskEntity.getName(), taskEntity.getStatus(), taskEntity.getProgress()));
        });
        /*ExchangisJobProgressVo jobProgressVo = new ExchangisJobProgressVo(TaskStatus.Running, 0.1);
        jobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo("5", "test-5", TaskStatus.Running, 0.1d));
        jobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo("6", "test-6", TaskStatus.Inited, 0.1d));
        jobProgressVo.addTaskProgress(new ExchangisJobProgressVo.ExchangisTaskProgressVo("7", "test-7", TaskStatus.Success, 1d));*/
        return jobProgressVo;
    }

    @Override
    public ExchangisJobProgressVo getJobStatus(String jobExecutionId) {
        LaunchedExchangisJobEntity launchedExchangisJobEntity = launchedJobDao.searchLaunchedJob(jobExecutionId);
        ExchangisJobProgressVo jobProgressVo = modelMapper.map(launchedExchangisJobEntity, ExchangisJobProgressVo.class);
        return jobProgressVo;
    }

    @Override
    public ExchangisLaunchedTaskMetricsVO getLaunchedTaskMetrics(String taskId, String jobExecutionId) {
        LaunchedExchangisTaskEntity launchedExchangisTaskEntity = launchedTaskDao.getLaunchedTaskMetrics(jobExecutionId, taskId);
        ExchangisLaunchedTaskMetricsVO exchangisLaunchedTaskVo = new ExchangisLaunchedTaskMetricsVO();
        exchangisLaunchedTaskVo.setTaskId(launchedExchangisTaskEntity.getTaskId());
        exchangisLaunchedTaskVo.setName(launchedExchangisTaskEntity.getName());
        exchangisLaunchedTaskVo.setStatus(launchedExchangisTaskEntity.getStatus().name());
        exchangisLaunchedTaskVo.setMetrics(launchedExchangisTaskEntity.getMetricsMap());
        /*Map<String, Object> metrics = new HashMap<>();
        ExchangisTaskResourceUsedMetricsDTO resourceUsed = new ExchangisTaskResourceUsedMetricsDTO();
        ExchangisTaskTrafficMetricsDTO traffic = new ExchangisTaskTrafficMetricsDTO();
        ExchangisTaskIndicatorMetricsDTO indicator = new ExchangisTaskIndicatorMetricsDTO();

        resourceUsed.setCpu(0.25);
        resourceUsed.setMemory((long) 20);

        traffic.setSource("mysql");
        traffic.setSink("hive");
        traffic.setFlow((long) 100);

        indicator.setExchangedRecords((long) 109345);
        indicator.setErrorRecords((long) 5);
        indicator.setIgnoredRecords((long) 5);

        metrics.put("resourceUsed", resourceUsed);
        metrics.put("traffic", traffic);
        metrics.put("indicator", indicator);

        ExchangisLaunchedTaskMetricsVO exchangisLaunchedTaskEntity = new ExchangisLaunchedTaskMetricsVO((long) 12, "task12", "running", metrics);*/

        return exchangisLaunchedTaskVo;
    }

    @Override
    public Message getJobLogInfo(String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows) {
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
        LinkedList<String> mockLogs = getMockLogs();
        int mockLogsSize = mockLogs.size();
        LinkedList<String> errLogs = new LinkedList<>();
        LinkedList<String> warningLogs = new LinkedList<>();
        LinkedList<String> infoLogs = new LinkedList<>();
        LinkedList<String> allLogs = new LinkedList<>();
        int endLine = from;
//        int actualSize = 0;
        if (Objects.nonNull(lastRows) && lastRows > 0) {
            for (int i = mockLogsSize - lastRows; i <= mockLogsSize; i++){
                allLogs.add(mockLogs.get(i));
                if (mockLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(mockLogs.get(i - 1));
                }
                else if (mockLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(mockLogs.get(i - 1));
                }
                else if (mockLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(mockLogs.get(i - 1));
                }
                endLine = mockLogsSize;
            }
        }
        else {
            for (int i = from, j = 0; (i <= mockLogsSize && j < size); i++, j++) {
                allLogs.add(mockLogs.get(i - 1));

                if (mockLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(mockLogs.get(i - 1));
                } else if (mockLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(mockLogs.get(i - 1));
                } else if (mockLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(mockLogs.get(i - 1));
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
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log");
        message.data("endLine", endLine);
        message.data("isEnd", from + size >= mockLogsSize);
        message.data("logs", logs);
        return message;
    }

    @Override
    public Message getTaskLogInfo(String taskId, String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows) {
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
        LinkedList<String> mockLogs = getMockLogs();
        int mockLogsSize = mockLogs.size();
        LinkedList<String> errLogs = new LinkedList<>();
        LinkedList<String> warningLogs = new LinkedList<>();
        LinkedList<String> infoLogs = new LinkedList<>();
        LinkedList<String> allLogs = new LinkedList<>();
        int endLine = from;
//        int actualSize = 0;
        if (Objects.nonNull(lastRows) && lastRows > 0) {
            for (int i = mockLogsSize - lastRows; i <= mockLogsSize; i++){
                allLogs.add(mockLogs.get(i));
                if (mockLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(mockLogs.get(i - 1));
                }
                else if (mockLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(mockLogs.get(i - 1));
                }
                else if (mockLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(mockLogs.get(i - 1));
                }
                endLine = mockLogsSize;
            }
        }
        else {
            for (int i = from, j = 0; (i <= mockLogsSize && j < size); i++, j++) {
                allLogs.add(mockLogs.get(i - 1));

                if (mockLogs.get(i - 1).contains("WARN")) {
                    warningLogs.add(mockLogs.get(i - 1));
                } else if (mockLogs.get(i - 1).contains("INFO")) {
                    infoLogs.add(mockLogs.get(i - 1));
                } else if (mockLogs.get(i - 1).contains("ERROR")) {
                    errLogs.add(mockLogs.get(i - 1));
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
        return message;
    }

    @Override
    public List<ExchangisLaunchedJobListVO> getExecutedJobList(Long jobId, String jobName, String status,
                                                               Long launchStartTime, Long launchEndTime, Integer  current, Integer size) {
        List<ExchangisLaunchedJobListVO> jobList = new ArrayList<>();
        Date date = new Date();
        ExchangisLaunchedJobListVO exchangisLaunchedJobListVO1 = new ExchangisLaunchedJobListVO((long) 23, "EMCM1", "作业1", date, (long) 555, "enjoyyin",
                "Succeed", 1.0, date, date);

        ExchangisLaunchedJobListVO exchangisLaunchedJobListVO2 = new ExchangisLaunchedJobListVO((long) 33, "EMCM2", "作业2", date, (long) 666, "tikazhang",
                "Running", 0.1, date, date);

        jobList.add(exchangisLaunchedJobListVO1);
        jobList.add(exchangisLaunchedJobListVO2);
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


}
