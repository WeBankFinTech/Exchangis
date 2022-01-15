package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.entity.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.entity.ExchangisLaunchedJobEntity;
import com.webank.wedatasphere.exchangis.job.server.entity.ExchangisLaunchedTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisExecutionService;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobAndTaskStatusVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobTaskListVO;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/1/8 15:25
 */
@RestController
@RequestMapping(value = "exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobExecutionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisJobExecutionController.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Resource
    private ExchangisExecutionService exchangisExecutionService;

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
        List<ExchangisJobTaskListVO> jobTaskList = exchangisExecutionService.getExecutedJobTaskList(jobExecutionId);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{id}/taskList");
        message.data("tasks", jobTaskList);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/progress", method = RequestMethod.GET)
    public Message getExecutedJobAndTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        ExchangisJobAndTaskStatusVO jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress");
        message.data("job", jobAndTaskStatus);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/status", method = RequestMethod.GET)
    public Message getExecutedJobStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{id}/status");
        message.data("status", "Running");
        message.data("progress", 0.1);
        return message;
    }

    @RequestMapping(value = "/execution/{jobExecutionId}/log", method = RequestMethod.GET)
    public Message getJobExecutionLogs(@PathVariable(value = "jobExecutionId") String jobExecutionId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                                        @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                                        @RequestParam(value = "lastRows", required = false) Integer lastRows) {

        return this.exchangisExecutionService.getJobLogInfo(jobExecutionId, fromLine, pageSize, ignoreKeywords, onlyKeywords, lastRows);
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/kill", method = RequestMethod.POST)
    public Message ExecutedJobKill(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        //ExchangisLaunchedJobEntity jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("Kill succeed(停止成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/kill");
        return message;
    }
}
