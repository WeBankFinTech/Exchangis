package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisCategoryLogVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedTaskMetricsVO;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/1/8 17:23
 */

@RestController
@RequestMapping(value = "exchangis/task", produces = {"application/json;charset=utf-8"})
public class ExchangisTaskExecuteController {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisTaskExecuteController.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Resource
    private JobExecuteService jobExecuteService;

    @RequestMapping( value = "/execution/{taskId}/metrics", method = RequestMethod.POST)
    public Message getTaskMetrics(@PathVariable("taskId") String taskId, @RequestBody Map<String, String> jobExecutionId) throws ExchangisJobServerException {
        ExchangisLaunchedTaskMetricsVO taskMetrics = this.jobExecuteService.getLaunchedTaskMetrics(taskId, jobExecutionId.get("jobExecutionId"));
        //return Message.ok("Submitted succeed(提交成功)！").data("task", taskMetrics);
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/task/execution/{taskId}/metrics");
        message.data("task", taskMetrics);
        return message;
    }

    @RequestMapping(value = "/execution/{taskId}/log", method = RequestMethod.GET)
    public Message getTaskExecutionLogs(@PathVariable(value = "taskId") String taskId,
                                        @RequestParam(value = "jobExecutionId", required = false) String jobExecutionId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                                        @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                                        @RequestParam(value = "lastRows", required = false) Integer lastRows, HttpServletRequest request){
        Message result = Message.ok("Submitted succeed(提交成功)！");
        LogQuery logQuery = new LogQuery(fromLine, pageSize,
                ignoreKeywords, onlyKeywords, lastRows);
        try {
            ExchangisCategoryLogVo categoryLogVo = this.jobExecuteService.getTaskLogInfo(taskId, jobExecutionId, logQuery, SecurityFilter.getLoginUsername(request));
            result.setData(Json.convert(categoryLogVo, Map.class, String.class, Object.class));
        } catch (Exception e) {
            String message = "Error occur while query task log: [task_id: " + taskId + ", job_execution_id: " + jobExecutionId +"]";
            LOG.error(message, e);
            result = Message.error(message + ", reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/exchangis/job/execution/{taskId}/log");
        return result;
    }
}
