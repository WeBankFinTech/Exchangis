package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobProgressVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobTaskVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedJobListVO;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/1/8 15:25
 */
@RestController
@RequestMapping(value = "exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobExecuteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisJobExecuteController.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Resource
    private JobExecuteService jobExecuteService;

    @RequestMapping( value = "/{id}/execute", method = RequestMethod.POST)
    public Message executeJob(@RequestBody(required = false) Map<String, Boolean> permitPartialFailures, @PathVariable("id") Long id) {
        String jobExecutionId = "555node1node2node3execId1";
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/{id}/execute");
        message.data("jobExecutionId", jobExecutionId);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/taskList", method = RequestMethod.GET)
    public Message getExecutedJobTaskList(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        List<ExchangisJobTaskVo> jobTaskList = jobExecuteService.getExecutedJobTaskList(jobExecutionId);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{id}/taskList");
        message.data("tasks", jobTaskList);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/progress", method = RequestMethod.GET)
    public Message getExecutedJobAndTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        ExchangisJobProgressVo jobAndTaskStatus = jobExecuteService.getExecutedJobProgressInfo(jobExecutionId);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress");
        message.data("job", jobAndTaskStatus);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/status", method = RequestMethod.GET)
    public Message getExecutedJobStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        ExchangisJobProgressVo jobStatus = jobExecuteService.getJobStatus(jobExecutionId);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{id}/status");
        message.data("status", jobStatus.getStatus());
        message.data("progress", jobStatus.getProgress());
        return message;
    }

    @RequestMapping(value = "/execution/{jobExecutionId}/log", method = RequestMethod.GET)
    public Message getJobExecutionLogs(@PathVariable(value = "jobExecutionId") String jobExecutionId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                                        @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                                        @RequestParam(value = "lastRows", required = false) Integer lastRows) {

        return this.jobExecuteService.getJobLogInfo(jobExecutionId, fromLine, pageSize, ignoreKeywords, onlyKeywords, lastRows);
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/kill", method = RequestMethod.POST)
    public Message ExecutedJobKill(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        //ExchangisLaunchedJobEntity jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("Kill succeed(停止成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/kill");
        return message;
    }

    @RequestMapping(value = "/listJobs", method = RequestMethod.GET)
    public Message listJobs(@RequestParam(value = "jobId", required = false) Long jobId,
                             @RequestParam(value = "jobName", required = false) String jobName,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "launchStartTime", required = false) Long launchStartTime,
                             @RequestParam(value = "launchEndTime", required = false) Long launchEndTime,
                             @RequestParam(value = "current", required = false) Integer current,
                             @RequestParam(value = "size", required = false) Integer size) {
        List<ExchangisLaunchedJobListVO> jobList = jobExecuteService.getExecutedJobList(jobId, jobName, status,
                launchStartTime, launchEndTime, current, size);
        int total = jobExecuteService.count(jobId, jobName, status, launchStartTime, launchEndTime);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/listJobs");
        message.data("jobList", jobList);
        message.data("total", total);
        return message;
    }

    @RequestMapping(value = "/partitionInfo", method = RequestMethod.GET)
    public Message listJobs() {
        Map<String, Object> render = new HashMap<>();
        List<String> partitionList = new ArrayList<>();
        List<String> partitionEmpty = new ArrayList<>();
        partitionList.add("$yyyy-MM-dd");
        partitionList.add("${run_date-1}");
        partitionList.add("${run_date-7}");
        partitionList.add("${run_month_begin-1}");
        render.put("key1", "");
        render.put("key2", "${yyyyMMdd}");
        render.put("key3", partitionList);
        render.put("key4", partitionEmpty);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/partionInfo/listJobs");
        message.data("type", "Map");
        message.data("render", render);
        return message;
    }
}
