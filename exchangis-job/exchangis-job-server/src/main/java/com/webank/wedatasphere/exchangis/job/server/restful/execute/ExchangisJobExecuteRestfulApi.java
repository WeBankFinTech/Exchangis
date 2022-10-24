package com.webank.wedatasphere.exchangis.job.server.restful.execute;

import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.OperationType;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.utils.JobAuthorityUtils;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisCategoryLogVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobProgressVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobTaskVo;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedJobListVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
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
@RequestMapping(value = "dss/exchangis/main/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobExecuteRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobExecuteRestfulApi.class);
    @Autowired
    private JobInfoService jobInfoService;

    @Resource
    private DefaultJobExecuteService executeService;

    /**
     * Execute job
     * @param permitPartialFailures permit
     * @param id id
     * @return message
     */
    @RequestMapping( value = "/{id}/execute", method = RequestMethod.POST)
    public Message executeJob(@RequestBody(required = false) Map<String, Object> permitPartialFailures,
                              @PathVariable("id") Long id, HttpServletRequest request) {
        String loginUser = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        Message result = Message.ok("Submitted succeed(提交成功)！");
        ExchangisJobInfo jobInfo = null;
        try {
            // First to find the job from the old table.
            ExchangisJobVo jobVo = jobInfoService.getJob(id, false);
            if (Objects.isNull(jobVo)){
                return Message.error("Job related the id: [" + id + "] is Empty(关联的任务不存在)");
            }
            // Convert to the job info
            jobInfo = new ExchangisJobInfo(jobVo);

            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_EXECUTE)){
                return Message.error("You have no permission to execute job (没有执行任务权限)");
            }

            // Send to execute service
            String jobExecutionId = executeService.executeJob(jobInfo, StringUtils.isNotBlank(jobInfo.getExecuteUser()) ?
                    jobInfo.getExecuteUser() : loginUser);
            result.data("jobExecutionId", jobExecutionId);
        } catch (Exception e) {
            String message;
            if (Objects.nonNull(jobInfo)) {
                message = "Error occur while executing job: [id: " + jobInfo.getId() + " name: " + jobInfo.getName() + "]";
                result = Message.error(message + "(执行任务出错), reason: " + e.getMessage());
            } else {
                message = "Error to get the job detail (获取任务信息出错)";
                result = Message.error(message);
            }
            LOG.error(message, e);
        }
        result.setMethod("/api/rest_j/v1/dss/exchangis/main/job/{id}/execute");
        assert jobInfo != null;
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, id.toString(), "Execute task is: " + jobInfo.getName(), OperateTypeEnum.EXECUTE, request);
        return result;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/taskList", method = RequestMethod.GET)
    public Message getExecutedJobTaskList(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get taskList (没有获取任务列表权限)");
            }
            List<ExchangisJobTaskVo> jobTaskList = executeService.getExecutedJobTaskList(jobExecutionId);
            message.data("tasks", jobTaskList);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while get taskList: [jobExecutionId: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + "(执行任务出错), reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/" + jobExecutionId + "/taskList");
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/progress", method = RequestMethod.GET)
    public Message getExecutedJobAndTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) {
        ExchangisJobProgressVo jobAndTaskStatus;
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get task progress (没有获取任务进度权限)");
            }
            jobAndTaskStatus = executeService.getExecutedJobProgressInfo(jobExecutionId);
        } catch (ExchangisJobServerException e) {
            // TODO Log exception
            return Message.error("Fail to get progress info (获取任务执行状态失败), reason: [" + e.getMessage() + "]");
        }
        Message message = Message.ok("Submitted succeed(提交成功)！");
        message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/" +jobExecutionId +"/progress");
        message.data("job", jobAndTaskStatus);
        return message;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/status", method = RequestMethod.GET)
    public Message getExecutedJobStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) {
        Message message = Message.ok("Submitted succeed(提交成功)！");
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get tastStatus (没有权限去获取任务状态)");
            }
            ExchangisJobProgressVo jobStatus = executeService.getJobStatus(jobExecutionId);
            message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/" + jobExecutionId + "/status");
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
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get logs (没有获取任务日志权限)");
            }

            ExchangisCategoryLogVo categoryLogVo = this.executeService
                    .getJobLogInfo(jobExecutionId, logQuery);
            result.setData(Json.convert(categoryLogVo, Map.class, String.class, Object.class));
        } catch (ExchangisJobServerException e) {
            String message = "Error occur while querying job log: [job_execution_id: " + jobExecutionId  +"]";
            LOG.error(message, e);
            result = Message.error(message + ", reason: " + e.getMessage());
        }
        result.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/{jobExecutionId}/log");
        return result;
    }

    @RequestMapping( value = "/execution/{jobExecutionId}/kill", method = RequestMethod.POST)
    public Message ExecutedJobKill(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) throws ExchangisJobServerException {
        ExchangisJobProgressVo jobStatus = executeService.getJobStatus(jobExecutionId);
        Message message = null;
        String loginUser = SecurityFilter.getLoginUsername(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_EXECUTE)) {
            return Message.error("You have no permission to get kill job (没有权限去杀死任务)");
        }
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
        message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/" + jobExecutionId + "/kill");
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, jobExecutionId, "Kill job: ", OperateTypeEnum.KILL, request);
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
        jobName = jobName.replace("_", "\\_");
        try {
            List<ExchangisLaunchedJobListVo> jobList = executeService.getExecutedJobList(jobExecutionId, jobName, status,
                    launchStartTime, launchEndTime, current, size, request);
            int total = executeService.count(jobExecutionId, jobName, status, launchStartTime, launchEndTime, request);
            message.data("jobList", jobList);
            message.data("total", total);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while getting job list: [job_execution_id: " + jobExecutionId  + "jobName: " + jobName  + "status: " + status  + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/execution/listJobs");
        return message;
    }

    @RequestMapping( value = "/{jobExecutionId}/deleteJob", method = RequestMethod.POST)
    public Message ExecutedJobDelete(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) throws ExchangisJobServerException {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to delete this record (没有删除历史记录权限)");
        }
        Message message = Message.ok("Kill succeed(停止成功)！");
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_EXECUTE)) {
                return Message.error("You have no permission to delete this record (没有删除历史记录权限)");
            }
            executeService.deleteJob(jobExecutionId);
            message.data("jobExecutionId", jobExecutionId);
        } catch (ExchangisJobServerException e){
            String errorMessage = "Error occur while delete job: [job_execution_id: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/" + jobExecutionId + "/deleteJob");
        return message;
    }

    @RequestMapping( value = "/{jobExecutionId}/allTaskStatus", method = RequestMethod.GET)
    public Message allTaskStatus(@PathVariable(value = "jobExecutionId") String jobExecutionId, HttpServletRequest request) throws ExchangisJobServerException {
        //ExchangisLaunchedJobEntity jobAndTaskStatus = exchangisExecutionService.getExecutedJobAndTaskStatus(jobExecutionId);
        Message message = Message.ok("所有任务状态");
        String loginUser = UserUtils.getLoginUser(request);
        try {
            if(!JobAuthorityUtils.hasJobExecuteSituationAuthority(loginUser, jobExecutionId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get tastStatus (没有权限去获取任务状态)");
            }
            List<String> allStatus = executeService.allTaskStatus(jobExecutionId);
            message.data("allStatus", allStatus);
            message.data("jobExecutionId", jobExecutionId);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while judge whether all task complete: [job_execution_id: " + jobExecutionId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        message.setMethod("/api/rest_j/v1/dss/exchangis/main/job/" + jobExecutionId + "/allTaskStatus");
        return message;
    }
}
