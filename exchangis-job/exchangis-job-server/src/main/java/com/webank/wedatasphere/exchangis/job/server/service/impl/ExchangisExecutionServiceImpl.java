package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.google.common.base.Joiner;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisExecutionService;
import org.apache.linkis.server.Message;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Objects;

@Service
public class ExchangisExecutionServiceImpl implements ExchangisExecutionService {

    @Override
    public Message getTaskLogInfo(String taskId, Integer fromLine, Integer pageSize) {
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
        for (int i = from, j = 0; (i <= mockLogsSize && j < size); i++,j++) {
            allLogs.add(mockLogs.get(i - 1));
            if ((i - 1) == warningLineIdx) {
                warningLogs.add(mockLogs.get(i - 1));
            } else {
                infoLogs.add(mockLogs.get(i - 1));
            }
            endLine = i;
//            actualSize++;
        }

        String allLogsStr = Joiner.on("\n").join(allLogs);
        String warningLogsStr = Joiner.on("\n").join(warningLogs);
        String infoLogsStr = Joiner.on("\n").join(infoLogs);
        String[] logs = new String[4];
        logs[0] = "";
        logs[1] = warningLogsStr;
        logs[2] = infoLogsStr;
        logs[3] = allLogsStr;

        Message message = Message.ok();
        message.setMethod("/exchangis/execution/tasks/{taskId}/logs");
        message.data("execID", taskId);
//        message.data("fromLine", from);
//        message.data("fetchSize", size);
//        message.data("actualSize", actualSize);
        message.data("logs", logs);
        message.data("endLine", endLine);
        message.data("isEnd", from + size >= mockLogsSize);
        return message;
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
