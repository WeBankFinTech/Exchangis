package com.webank.wedatasphere.exchangis.job.server.restful.external;

import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.IProjectCopyService;
import com.webank.wedatasphere.exchangis.job.server.service.IProjectImportService;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.ProjectImportServerImpl;
import com.webank.wedatasphere.exchangis.job.server.utils.AuthorityUtils;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.mapper.ProjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import javax.ws.rs.core.Context;
import java.rmi.ServerException;
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

    @Resource
    private ProjectImportServerImpl projectImportServer;

    @Resource
    private IProjectCopyService projectCopyService;

    @Autowired
    private ProjectMapper projectMapper;

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
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setCreateUser(userName);
        Message response = Message.ok();
        /*if (!AuthorityUtils.hasOwnAuthority(exchangisJobVo.getProjectId(), userName) && !AuthorityUtils.hasEditAuthority(exchangisJobVo.getProjectId(), userName)) {
            return Message.error("You have no permission to create (没有编辑权限，无法创建任务)");
        }*/
        try{
            Long id = null;
            id = jobInfoService.createJob(exchangisJobVo).getId();
            response.data("id", id);
            LOG.info("id6666: {}", id);
        } catch (Exception e){
            String message = "Fail to create dss job: " + exchangisJobVo.getJobName() +" (创建DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
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
        String userName = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok("dss job deleted");
        try {
            LOG.info("delete job bean: {}, jobid: {}", jobInfoService.getJob(id, true), jobInfoService.getJob(id, true).getId());
            if (Objects.isNull(jobInfoService.getJob(id, true)) || jobInfoService.getJob(id, true).getId() == null){
                return response;
            }
            else if (!hasAuthority(userName, jobInfoService.getJob(id, true))) {
                return Message.error("You have no permission to update (没有删除权限)");
            }
            /*else if (!AuthorityUtils.hasOwnAuthority(jobInfoService.getJob(id, true).getProjectId(), userName) && !AuthorityUtils.hasEditAuthority(jobInfoService.getJob(id, true).getProjectId(), userName)) {
                return Message.error("You have no permission to delete (没有编辑权限，无法删除)");
            }*/
            jobInfoService.deleteJob(id);
        } catch (Exception e){
            String message = "Fail to delete dss job [ id: " + id + "] (删除DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
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
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setId(id);
        exchangisJobVo.setModifyUser(userName);
        Message response = Message.ok();
        try{
            LOG.info("update job bean: {}, jobid: {}", jobInfoService.getJob(id, true), jobInfoService.getJob(id, true).getId());
            if (Objects.isNull(jobInfoService.getJob(id, true)) || jobInfoService.getJob(id, true).getId() == null){
                return Message.error("You have no job in exchangis,please delete this job (该节点在exchangis端不存在，请删除该节点)");
            }
            else if (!hasAuthority(userName, jobInfoService.getJob(id , true))) {
                return Message.error("You have no permission to update (没有更新权限)");
            }
            /*else if (!AuthorityUtils.hasOwnAuthority(exchangisJobVo.getProjectId(), userName) && !AuthorityUtils.hasEditAuthority(exchangisJobVo.getProjectId(), userName)) {
                return Message.error("You have no permission to update (没有编辑权限，无法更新项目)");
            }*/
            response.data("id", jobInfoService.updateJob(exchangisJobVo).getId());
        } catch (Exception e){
            String message = "Fail to update dss job: " + exchangisJobVo.getJobName() +" (更新DSS任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Execute job
     * @param id id
     * @return message
     */
    @RequestMapping( value = "/execute/{id}", method = RequestMethod.POST)
    public Message executeJob(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String submitUser = params.get("submitUser").toString();
        String loginUser = SecurityFilter.getLoginUsername(request);
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
            //find project info
            ExchangisProject project = projectMapper.getDetailById(jobVo.getProjectId());
            LOG.info("project: {}, getProjectId:{}",project,jobVo.getProjectId());
            //find project user authority
            if (!hasExecuteAuthority(submitUser, project)){
                return Message.error("You have no permission to execute job (没有执行DSS任务权限)");
            }
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
        return result;
    }

    @RequestMapping( value = "/import", method = RequestMethod.POST)
    public Message importJob(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException, ExchangisJobServerException{

        Message response = null;
        try {
            LOG.info("param: {}", params);
            response = projectImportServer.importProject(request, params);
            LOG.info("import job success");
        } catch (ExchangisJobServerException e){
            String message = "Fail import job [ id: " + params + "] (导入任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;

    }

    @RequestMapping( value = "/export", method = RequestMethod.POST)
    public Message exportJob(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException, ExchangisJobServerException {
        String userName = SecurityFilter.getLoginUsername(request);

        LOG.info("export function params: {}", params);
        Message response = null;
        try {
            response = jobInfoService.exportProject(params, userName, request);
            LOG.info("export job success");
        } catch (Exception e){
            String message = "Fail Export job [ id: " + params + "] (导出任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;

        //return jobInfoService.exportProject(params, userName, request);

    }

    @RequestMapping( value = "/copy", method = RequestMethod.POST)
    public Message copy(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException {
        String userName = SecurityFilter.getLoginUsername(request);

        LOG.info("copy function params: {}", params);
        Message response = null;
        try {
            response = projectCopyService.copy(params, userName, request);
            LOG.info("copy node success");
        } catch (Exception e){
            String message = "Fail Copy project [ id: " + params + "] (导出任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;

        //return jobInfoService.exportProject(params, userName, request);

    }

    /**
     * TODO complete the authority strategy
     * @param username username
     * @param job job
     * @return
     */
    private boolean hasAuthority(String username, ExchangisJobVo job){
        return Objects.nonNull(job) && username.equals(job.getCreateUser());
    }

    /**
     * @param username username
     * @param project project
     * @return
     */
    private boolean hasExecuteAuthority(String username, ExchangisProject project){
        if(project.getEditUsers().contains(username)||project.getExecUsers().contains(username)){
            return true;
        }
        return false;
    }
}
