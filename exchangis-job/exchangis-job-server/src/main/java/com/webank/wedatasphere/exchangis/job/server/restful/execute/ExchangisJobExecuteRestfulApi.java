package com.webank.wedatasphere.exchangis.job.server.restful.execute;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisCategoryLogVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobProgressVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobTaskVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedJobListVO;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 * @Date 2022/1/8 15:25
 */
@RestController
@RequestMapping(value = "exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobExecuteRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobExecuteRestfulApi.class);
    @Autowired
    private ExchangisJobService exchangisJobService;

    @Resource
    private DefaultJobExecuteService executeService;

    /**
     * Execute job
     * @param permitPartialFailures permit
     * @param id id
     * @return message
     */
    @RequestMapping( value = "/{id}/execute", method = RequestMethod.POST)
    public Message executeJob(@RequestBody(required = false) Map<String, Boolean> permitPartialFailures,
                              @PathVariable("id") Long id, HttpServletRequest request) {
        // First to find the job from the old table. TODO use the job entity
        ExchangisJobVO jobVo = exchangisJobService.getById(id);
        if (Objects.isNull(jobVo)){
            return Message.error("Job related the id: [" + id + "] is Empty(关联的任务不存在)");
        }
        // Convert to the job info TODO cannot find the execute user
        ExchangisJobInfo jobInfo = new ExchangisJobInfo(jobVo);
        String loginUser = SecurityFilter.getLoginUsername(request);
        if (!hasAuthority(loginUser, jobInfo)){
            return Message.error("You have no permission to execute job (没有执行任务权限)");
        }
        Message result = Message.ok("Submitted succeed(提交成功)！");
        try {
            // Send to execute service
            String jobExecutionId = executeService.executeJob(jobInfo, StringUtils.isNotBlank(jobInfo.getExecuteUser()) ?
                    jobInfo.getExecuteUser() : loginUser);
            result.data("jobExecutionId", jobExecutionId);
        } catch (ExchangisJobServerException e) {
            String message = "Error occur while executing job: [id: " + jobInfo.getId() + " name: " + jobInfo.getName() +"]";
            LOG.error(message, e);
            result = Message.error(message + "(执行任务出错), reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/exchangis/job/{id}/execute");
        return result;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/taskList", method = RequestMethod.GET)
    public Message getExecutedJobTaskList(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        try {
            List<ExchangisJobTaskVo> jobTaskList = executeService.getExecutedJobTaskList(jobExecutionId);
            message.data("tasks", jobTaskList);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while get taskList: [jobExecutionId: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + "(执行任务出错), reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/" + jobExecutionId + "/taskList");
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/progress", method = RequestMethod.GET)
    public Message getExecutedJobAndTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        ExchangisJobProgressVo jobAndTaskStatus;
        try {
            jobAndTaskStatus = executeService.getExecutedJobProgressInfo(jobExecutionId);
        } catch (ExchangisJobServerException e) {
            // TODO Log exception
            return Message.error("Fail to get progress info (获取任务执行状态失败), reason: [" + e.getMessage() + "]");
        }
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/" +jobExecutionId +"/progress");
        message.data("job", jobAndTaskStatus);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/status", method = RequestMethod.GET)
    public Message getExecutedJobStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        try {
            ExchangisJobProgressVo jobStatus = executeService.getJobStatus(jobExecutionId);
            message.setMethod("/api/rest_j/v1/exchangis/job/execution/" + jobExecutionId + "/status");
            message.data("status", jobStatus.getStatus());
            message.data("progress", jobStatus.getProgress());
            message.data("allTaskStatus", jobStatus.getAllTaskStatus());
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while getting job status: [job_execution_id: " + jobExecutionId  +"]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        return message;
    }

    @RequestMapping(value = "/execution/{jobExecutionId}/log", method = RequestMethod.GET)
    public Message getJobExecutionLogs(@PathVariable(value = "jobExecutionId") String jobExecutionId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                                        @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                                        @RequestParam(value = "lastRows", required = false) Integer lastRows, HttpServletRequest request) {

        Message result = Message.ok("Submitted succeed(提交成功)！");
        LogQuery logQuery = new LogQuery(fromLine, pageSize,
                ignoreKeywords, onlyKeywords, lastRows);
        try {
            ExchangisCategoryLogVo categoryLogVo = this.executeService
                    .getJobLogInfo(jobExecutionId, logQuery, SecurityFilter.getLoginUsername(request));
            result.setData(Json.convert(categoryLogVo, Map.class, String.class, Object.class));
        } catch (ExchangisJobServerException e) {
            String message = "Error occur while querying job log: [job_execution_id: " + jobExecutionId  +"]";
            LOG.error(message, e);
            result = Message.error(message + ", reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log");
        return result;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/kill", method = RequestMethod.POST)
    public Message ExecutedJobKill(@PathVariable(value = "jobExecutionId") String jobExecutionId) throws ExchangisJobServerException {
        ExchangisJobProgressVo jobStatus = executeService.getJobStatus(jobExecutionId);
        String status = jobStatus.getStatus().toString();
        Message message = null;
        if (!TaskStatus.isCompleted(jobStatus.getStatus()))
        {
            message = Message.ok("Kill succeed(停止成功)！");
            try {
                executeService.killJob(jobExecutionId);
            } catch (ExchangisJobServerException e) {
                String errorMessage = "Error occur while killing job: [job_execution_id: " + jobExecutionId + "]";
                LOG.error(errorMessage, e);
                message = Message.error(message + ", reason: " + e.getMessage());
            }
        }
        else {
            message = Message.error("Kill failed(停止失败)！,job 已经到终态");
        }
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/" + jobExecutionId + "/kill");
        return message;
    }

    @RequestMapping(value = "/listJobs", method = RequestMethod.GET)
    public Message listJobs(@RequestParam(value = "jobExecutionId", required = false) String jobExecutionId,
                             @RequestParam(value = "jobName", required = false) String jobName,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "launchStartTime", required = false) Long launchStartTime,
                             @RequestParam(value = "launchEndTime", required = false) Long launchEndTime,
                             @RequestParam(value = "current", required = false) int current,
                             @RequestParam(value = "size", required = false) int size,
                            HttpServletRequest request) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        try {
            List<ExchangisLaunchedJobListVO> jobList = executeService.getExecutedJobList(jobExecutionId, jobName, status,
                    launchStartTime, launchEndTime, current, size, request);
            int total = executeService.count(jobExecutionId, jobName, status, launchStartTime, launchEndTime, request);
            message.data("jobList", jobList);
            message.data("total", total);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while getting job list: [job_execution_id: " + jobExecutionId  + "jobName: " + jobName  + "status: " + status  + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/exchangis/job/execution/listJobs");
        return message;
    }

    @RequestMapping(value = "/partitionInfo", method = RequestMethod.GET)
    public Message partitionInfo(@RequestParam(value = "dataSourceType", required = false) String dataSourceTpe,
                            @RequestParam(value = "dbname", required = false) String dbname,
                            @RequestParam(value = "table", required = false) String table) {
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

    /**
     * TODO complete the authority strategy
     * @param username username
     * @param jobInfo job info
     * @return
     */
    private boolean hasAuthority(String username, ExchangisJobInfo jobInfo){
        return username.equals(jobInfo.getCreateUser());
    }

    @RequestMapping( value = "/{jobExecutionId}/deleteJob", method = RequestMethod.POST)
    public Message ExecutedJobDelete(@PathVariable(value = "jobExecutionId") String jobExecutionId) throws ExchangisJobServerException {
        //ExchangisLaunchedJobEntity jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("Kill succeed(停止成功)！");
        try {
            executeService.deleteJob(jobExecutionId);
            message.data("jobExecutionId", jobExecutionId);
        } catch (ExchangisJobServerException e){
            String errorMessage = "Error occur while delete job: [job_execution_id: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/exchangis/job/" + jobExecutionId + "/deleteJob");
        return message;
    }

    @RequestMapping( value = "/{jobExecutionId}/allTaskStatus", method = RequestMethod.GET)
    public Message allTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId) throws ExchangisJobServerException {
        //ExchangisLaunchedJobEntity jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("所有任务状态");
        try {
            List<String> allStatus = executeService.allTaskStatus(jobExecutionId);
            message.data("allStatus", allStatus);
            message.data("jobExecutionId", jobExecutionId);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while judge whether all task complete: [job_execution_id: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/exchangis/job/" + jobExecutionId + "/allTaskStatus");
        return message;
    }
}
