package com.webank.wedatasphere.exchangis.job.server.restful.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.OperationType;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.utils.JobAuthorityUtils;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.util.Map;
import java.util.Objects;

/**
 * Define to support the app conn, in order to distinguish from the inner api
 */
@RestController
@RequestMapping(value = "/dss/exchangis/main/appJob", produces = {"application/json;charset=utf-8"})
public class ExchangisJobDssAppConnRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobDssAppConnRestfulApi.class);
    /**
     * Job service
     */
    @Resource
    private JobInfoService jobInfoService;

    /**
     * Job execute service
     */
    @Resource
    private DefaultJobExecuteService executeService;

    /**
     * Create job
     * @param request http request
     * @param exchangisJobVo exchangis job vo
     * @return message
     */
    @RequestMapping( value = "/create", method = RequestMethod.POST)
    public Message createJob(
            @Validated({InsertGroup.class, Default.class}) @RequestBody ExchangisJobVo exchangisJobVo,
            BindingResult result,
            HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to create (没有创建任务权限)");
        }
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        exchangisJobVo.setCreateUser(loginUser);
        Message response = Message.ok();

        Long id = null;
        try{
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, exchangisJobVo.getProjectId(), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to create Job (没有创建任务权限)");
            }
            id = jobInfoService.createJob(exchangisJobVo).getId();
            response.data("id", id);
            LOG.info("job id is: {}", id);
        } catch (Exception e){
            String message = "Fail to create dss job: " + exchangisJobVo.getJobName() +" (创建DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        assert id != null;
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, id.toString(), "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.CREATE, request);
        return response;
    }

    /**
     * Delete job
     * @param id id
     * @param request http request
     * @return message
     */
    @RequestMapping( value = "/{id:\\d+}", method = RequestMethod.POST)
    public Message deleteJob(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有删除任务权限)");
        }
        String loginUser = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok("dss job deleted");
        try {
            ExchangisJobVo exchangisJob = jobInfoService.getJob(id, true);
            if (Objects.isNull(exchangisJob)){
                return response;
            }
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, exchangisJob.getProjectId(), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to delete (没有删除任务权限)");
            }
            jobInfoService.deleteJob(id);
        } catch (Exception e){
            String message = "Fail to delete dss job [ id: " + id + "] (删除DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, id.toString(), "Job", OperateTypeEnum.DELETE, request);
        return response;
    }

    /**
     * Update job
     * @param id job id
     * @param exchangisJobVo job vo
     * @return message
     */
    @RequestMapping( value = "/{id:\\d+}", method = RequestMethod.PUT)
    public Message updateJob(@PathVariable("id") Long id,
                             @Validated @RequestBody ExchangisJobVo exchangisJobVo,
                             BindingResult result, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有更新任务权限)");
        }
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        exchangisJobVo.setId(id);
        exchangisJobVo.setModifyUser(loginUser);
        Message response = Message.ok();
        try{
            LOG.info("update job bean: {}, jobid: {}", jobInfoService.getJob(id, true), jobInfoService.getJob(id, true).getId());
            ExchangisJobVo exchangisJob = jobInfoService.getJob(id, true);
            if (Objects.isNull(exchangisJob)){
                return Message.error("You have no job in exchangis,please delete this job (该节点在exchangis端不存在，请删除该节点)");
            }
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to update (没有更新任务权限)");
            }
            response.data("id", jobInfoService.updateJob(exchangisJobVo).getId());
        } catch (Exception e){
            String message = "Fail to update dss job: " + exchangisJobVo.getJobName() +" (更新DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, id.toString(), "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.UPDATE, request);
        return response;
    }

    /**
     * Execute job
     * @param id id
     * @return message
     */
    @RequestMapping( value = "/execute/{id}", method = RequestMethod.POST)
    public Message executeJob(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody Map<String, Object> params) {
        try {
            LOG.info("start to parse params");
            String paramString = BDPJettyServerHelper.jacksonJson().writeValueAsString(params);
            LOG.error("paramString: {}", paramString);
        } catch (JsonProcessingException e) {
            LOG.error("parse execute content error: {}", e.getMessage());
        }
        String submitUser = params.get("submitUser").toString();
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        Message result = Message.ok();
        ExchangisJobInfo jobInfo = null;
        LOG.info("wds execute user: {}", loginUser);
        try {
            // First to find the job from the old table.
            ExchangisJobVo jobVo = jobInfoService.getJob(id, false);
           /* if (!AuthorityUtils.hasOwnAuthority(jobVo.getProjectId(), loginUser) && !AuthorityUtils.hasExecAuthority(jobVo.getProjectId(), loginUser)) {
                return Message.error("You have no permission to execute job (没有执行任务权限)");
            }*/
            if (Objects.isNull(jobVo)){
                return Message.error("Job related the id: [" + id + "] is Empty(关联的DSS任务不存在)");
            }
            // Convert to the job info
            jobInfo = new ExchangisJobInfo(jobVo);
            jobInfo.setName(jobVo.getJobName());
            jobInfo.setId(jobVo.getId());
            LOG.info("jobInfo: name{},executerUser{},createUser{},id{}",jobInfo.getName(),jobInfo.getExecuteUser(),jobInfo.getCreateUser(),jobInfo.getId());
            LOG.info("loginUser: {}, jobVo:{}",loginUser,jobVo);
            //find project user authority
            /*if (!hasAuthority(submitUser, jobVo)){
                return Message.error("You have no permission to execute job (没有执行DSS任务权限)");
            }*/
            // Send to execute service
            String jobExecutionId = executeService.executeJob(jobInfo, StringUtils.isNotBlank(jobInfo.getExecuteUser()) ?
                    jobInfo.getExecuteUser() : loginUser);
            result.data("jobExecutionId", jobExecutionId);

            LOG.info("Prepare to get job status");
            /*while (true) {
                TaskStatus jobStatus = executeService.getJobStatus(jobExecutionId).getStatus();
                LOG.info("Taskstatus is: {}", jobStatus.name());
                if (jobStatus == TaskStatus.Success ) {
                    result.data("jobStatus", jobStatus.name());
                    LOG.info("Execute task success");
                    break;
                } else if (jobStatus == TaskStatus.Cancelled || jobStatus == TaskStatus.Failed || jobStatus == TaskStatus.Undefined || jobStatus == TaskStatus.Timeout) {
                    result.data("jobStatus", jobStatus.name());
                    LOG.info("Execute task faild");
                    throw new Exception();
                }
            }*/
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
        assert jobInfo != null;
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, id.toString(), "Execute task is: " + jobInfo.getName(), OperateTypeEnum.EXECUTE, request);
        return result;
    }
}
