package com.webank.wedatasphere.exchangis.job.server.restful.execute;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.OperationType;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.utils.JobAuthorityUtils;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisCategoryLogVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedTaskMetricsVo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.METRICS_OP_ERROR;

/**
 * @Date 2022/1/8 17:23
 */

@RestController
@RequestMapping(value = "dss/exchangis/main/task", produces = {"application/json;charset=utf-8"})
public class ExchangisTaskExecuteRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisTaskExecuteRestfulApi.class);
    @Autowired
    private JobInfoService jobInfoService;

    @Resource
    private JobExecuteService jobExecuteService;

    @Resource
    private DefaultJobExecuteService executeService;

    @RequestMapping(value = "/execution/{taskId}/metrics", method = RequestMethod.POST)
    public Message getTaskMetrics(@PathVariable("taskId") String taskId,
                                  @RequestBody Map<String, Object> json, HttpServletRequest request) throws ExchangisJobServerException {
        Message result = Message.ok("Submitted succeed(提交成功)！");
        String jobExecutionId = null;

        if (null != json.get("jobExecutionId")) {
            jobExecutionId = (String) json.get("jobExecutionId");
        }
        if (StringUtils.isBlank(jobExecutionId)) {
            return Message.error("Required params 'jobExecutionId' is missing");
        }
        try {

            if (!JobAuthorityUtils.hasJobExecuteSituationAuthority(UserUtils.getLoginUser(request), jobExecutionId, OperationType.JOB_EXECUTE)) {

                throw new ExchangisJobServerException(METRICS_OP_ERROR.getCode(), "Unable to find the launched job by [" + jobExecutionId + "]", null);
            }
            ExchangisLaunchedTaskMetricsVo taskMetrics = this.jobExecuteService.getLaunchedTaskMetrics(taskId, jobExecutionId);
            result.data("task", taskMetrics);
        } catch (Exception e) {
            String message = "Error occur while fetching metrics: [task_id: " + taskId + ", job_execution_id: " + jobExecutionId + "]";
            LOG.error(message, e);
            result = Message.error(message + ", reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/dss/exchangis/main/task/execution/{taskId}/metrics");
        return result;
    }

    @RequestMapping(value = "/execution/{taskId}/log", method = RequestMethod.GET)
    public Message getTaskExecutionLogs(@PathVariable(value = "taskId") String taskId,
                                        @RequestParam(value = "jobExecutionId", required = false) String jobExecutionId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                                        @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                                        @RequestParam(value = "lastRows", required = false) Integer lastRows, HttpServletRequest request) {
        Message result = Message.ok("Submitted succeed(提交成功)！");
        LogQuery logQuery = new LogQuery(fromLine, pageSize,
                ignoreKeywords, onlyKeywords, lastRows);
        String userName = UserUtils.getLoginUser(request);
        try {
            if (!JobAuthorityUtils.hasJobExecuteSituationAuthority(userName, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get logs(没有查看日志权限)");
            }

            ExchangisCategoryLogVo categoryLogVo = this.jobExecuteService.getTaskLogInfo(taskId, jobExecutionId, logQuery);
            result.setData(Json.convert(categoryLogVo, Map.class, String.class, Object.class));
        } catch (Exception e) {
            String message = "Error occur while query task log: [task_id: " + taskId + ", job_execution_id: " + jobExecutionId + "]";
            LOG.error(message, e);
            result = Message.error(message + ", reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/{taskId}/log");
        return result;
    }
}
