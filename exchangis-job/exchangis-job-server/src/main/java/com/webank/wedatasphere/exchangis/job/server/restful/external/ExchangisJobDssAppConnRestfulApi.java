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
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
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
import java.util.Optional;

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

    @Resource
    private ProjectOpenService projectOpenService;
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
        String originUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        exchangisJobVo.setCreateUser(loginUser);
        Message response = Message.ok();

        Long id = null;
        try{
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, Long.parseLong(exchangisJobVo.getProjectId()), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to create Job (没有创建任务权限)");
            }
            id = jobInfoService.createJob(exchangisJobVo).getId();
            response.data("id", id);
            LOG.info("job id is: {}", id);
        } catch (Exception e){
            String message = "Fail to create dss job: " + exchangisJobVo.getJobName() +" (创建DSS任务失败)";
            LOG.error(message, e);
            return Message.error(message);
        }
        assert id != null;
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.JOB, String.valueOf(id), "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.CREATE, request);
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
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, Long.parseLong(exchangisJob.getProjectId()), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to delete (没有删除任务权限)");
            }
            jobInfoService.deleteJob(id);
        } catch (Exception e){
            String message = "Fail to delete dss job [ id: " + id + "] (删除DSS任务失败)";
            LOG.error(message, e);
            return Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, String.valueOf(id), "Job", OperateTypeEnum.DELETE, request);
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
            LOG.info("update job bean: {}, job id: {}", jobInfoService.getJob(id, true), jobInfoService.getJob(id, true).getId());
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
            return Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, loginUser, TargetTypeEnum.JOB, String.valueOf(id), "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.UPDATE, request);
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
            LOG.info("Start to parse params from dss job execution request");
            String paramString = BDPJettyServerHelper.jacksonJson().writeValueAsString(params);
            LOG.info("Success to parse params content: {}", paramString);
        } catch (JsonProcessingException e) {
            LOG.error("Parse execute content error: {}", e.getMessage());
        }
        String execUser = Optional.ofNullable(params.get("execUser")).orElse("").toString();
        String originUser = SecurityFilter.getLoginUsername(request);
        String loginUser = UserUtils.getLoginUser(request);
        Message response = Message.ok();
        ExchangisJobInfo jobInfo = null;
        try {
            // First to find the job from the old table.
            ExchangisJobVo jobVo = jobInfoService.getJob(id, false);
            if (Objects.isNull(jobVo)){
                return Message.error("Job related the id: [" + id + "] is Empty(关联的DSS任务不存在)");
            }
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_EXECUTE)) {
                return Message.error("You have no permission to execute job (没有执行任务权限)");
            }
            // Convert to the job info
            jobInfo = new ExchangisJobInfo(jobVo);
            jobInfo.setName(jobVo.getJobName());
            jobInfo.setId(jobVo.getId());
            execUser = StringUtils.isNotBlank(execUser)? execUser : jobInfo.getExecuteUser();
            LOG.info("Execute dss job name: [{}], id: [{}], createUser: [{}], execUser: [{}], loginUser: [{}]",
                    jobInfo.getName(), jobInfo.getId(), jobInfo.getCreateUser(),
                    execUser, loginUser);
            // Send to execute service, just use login user(execute user) from dss
            String jobExecutionId = executeService.executeJob(loginUser, jobInfo, loginUser);
            response.data("jobExecutionId", jobExecutionId);
            LOG.info("Prepare to get job status");
        } catch (Exception e) {
            String message;
            if (Objects.nonNull(jobInfo)) {
                message = "Error occur while executing job: [id: " + jobInfo.getId() + " name: " + jobInfo.getName() + "]";
                response = Message.error(message + "(执行任务出错), reason: " + e.getMessage());
            } else {
                message = "Error to get the job detail (获取任务信息出错), reason: " + e.getMessage();
                response = Message.error(message);
            }
            LOG.error(message, e);
            return response;
        }
        AuditLogUtils.printLog(originUser, loginUser, TargetTypeEnum.JOB, String.valueOf(id), "Execute task is: " + jobInfo.getName(), OperateTypeEnum.EXECUTE, request);
        return response;
    }
}
