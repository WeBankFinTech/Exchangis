package com.webank.wedatasphere.exchangis.job.server.restful;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.job.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.job.domain.OperationType;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.launcher.ExchangisLauncherConfiguration;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.JobFuncService;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.utils.JobAuthorityUtils;
import com.webank.wedatasphere.exchangis.job.server.vo.JobFunction;
import com.webank.wedatasphere.exchangis.job.utils.AuditLogUtils;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.ProxyUserSSOUtils;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scala.Option;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;


/**
 * The basic controller of Exchangis job
 */
@RestController
@RequestMapping(value = "dss/exchangis/main/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobRestfulApi.class);

    /**
     * Job service
     */
    @Resource
    private JobInfoService jobInfoService;

    @Resource
    private ProjectService projectService;

    @Resource
    private JobFuncService jobFuncService;

    /**
     * Query job in page
     *
     * @param projectId project id
     * @param jobType   job type
     * @param name      name
     * @param current   current
     * @param size      size
     * @param request   request
     * @return message
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Message getJobList(@RequestParam(value = "projectId") Long projectId,
                              @RequestParam(value = "jobType", required = false) String jobType,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "current", required = false) int current,
                              @RequestParam(value = "size", required = false) int size,
                              HttpServletRequest request) {
        ExchangisJobQueryVo queryVo = new ExchangisJobQueryVo(
                projectId, jobType, name, current, size
        );
        String loginUser = SecurityFilter.getLoginUsername(request);
        try {
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, projectId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to create Job (没有查询任务权限)");
            }

            queryVo.setCreateUser(loginUser);
            PageResult<ExchangisJobVo> pageResult = jobInfoService.queryJobList(queryVo);
            return Message.ok().data("total", pageResult.getTotal()).data("result", pageResult.getList());
        } catch (Exception e) {
            LOG.error("Fail to query job list for user {}", loginUser, e);
            return Message.error("Failed to query job list (获取任务列表失败)");
        }
    }

    /**
     * Engine list
     *
     * @return message
     */
    @RequestMapping(value = "/engineType", method = RequestMethod.GET)
    public Message getEngineList() {
        // TODO limit the engine type in exchangis
//        return Message.ok().data("result", EngineTypeEnum.values());
        return Message.ok().data("result", new EngineTypeEnum[]{EngineTypeEnum.SQOOP, EngineTypeEnum.DATAX});
    }

    /**
     * Executor
     *
     * @return message
     */
    @RequestMapping(value = "/Executor", method = RequestMethod.GET)
    public Message getExecutor(HttpServletRequest request) {
        Option<String> proxyUserUsername =
                ProxyUserSSOUtils.getProxyUserUsername(request);
        String loginUser = SecurityFilter.getLoginUsername(request);
        List<String> executor = new ArrayList<>();
        if (proxyUserUsername.isDefined()) {
            executor.add(proxyUserUsername.get());
        } else {
            executor.add(loginUser);
        }
        executor.add("hadoop");
        return Message.ok().data("result", executor);
    }


    /**
     * function
     *
     * @return message
     */
    @RequestMapping(value = "/func/{funcType:\\w+}", method = RequestMethod.GET)
    public Message vjobFuncList(@PathVariable("funcType") String funcType,
                                HttpServletRequest request) {
        return jobFuncList("DATAX", funcType, request);
    }

    @RequestMapping(value = "/func/{tabName:\\w+}/{funcType:\\w+}", method = RequestMethod.GET)
    public Message jobFuncList(@PathVariable("tabName") String tabName,
                               @PathVariable("funcType") String funcType,
                               HttpServletRequest request) {

        Message response = Message.ok();
        try {
            //Limit that the tab should be an engine tab
            EngineTypeEnum.valueOf(tabName.toUpperCase());
            JobFunction.FunctionType funcTypeEnum = JobFunction.FunctionType.valueOf(funcType.toUpperCase());
            List<JobFunction> functionList = jobFuncService.getFunctions(tabName, funcTypeEnum);
            return Message.ok().data("data", functionList);
        } catch (Exception e) {
            String message = "Fail to get function (获取函数失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Create job
     *
     * @param request        http request
     * @param exchangisJobVo exchangis job vo
     * @return message
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Message createJob(
            @Validated({InsertGroup.class, Default.class}) @RequestBody ExchangisJobVo exchangisJobVo,
            BindingResult result,
            HttpServletRequest request) throws ExchangisProjectErrorException {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to create Job (没有创建任务权限)");
        }
        if (result.hasErrors()) {
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String loginUser = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setCreateUser(loginUser);
        Message response = Message.ok();

        try {
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, exchangisJobVo.getProjectId(), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to create Job (没有创建任务权限)");
            }
            response.data("result", jobInfoService.createJob(exchangisJobVo));
        } catch (Exception e) {
            String message = "Fail to create job: " + exchangisJobVo.getJobName() + " (创建任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        LOG.info("start to print audit log");
        AuditLogUtils.printLog(loginUser, TargetTypeEnum.JOB,"0", "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.CREATE,request);
        return response;
    }

    /**
     * Copy job
     *
     * @param sourceJobId    source job id
     * @param exchangisJobVo job vo
     * @return message
     */
    @RequestMapping(value = "/{sourceJobId:\\d+}/copy", method = RequestMethod.POST)
    public Message copyJob(@PathVariable("sourceJobId") Long sourceJobId,
                           @Validated @RequestBody ExchangisJobVo exchangisJobVo,
                           BindingResult result, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有复制权限)");
        }
        if (result.hasErrors()) {
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String loginUser = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setId(sourceJobId);
        exchangisJobVo.setModifyUser(loginUser);
        Message response = Message.ok();
        try {
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, exchangisJobVo.getProjectId(), OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to update (没有作业复制权限)");
            }
            response.data("result", jobInfoService.copyJob(exchangisJobVo));
        } catch (Exception e) {
            String message = "Fail to update job: " + exchangisJobVo.getJobName() + " (复制任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
        //todo return Message.error("Function will be supported in next version (该功能将在下版本支持)");
    }

    /**
     * Update job
     *
     * @param id             job id
     * @param exchangisJobVo job vo
     * @return message
     */
    @RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PUT)
    public Message updateJob(@PathVariable("id") Long id,
                             @Validated @RequestBody ExchangisJobVo exchangisJobVo,
                             BindingResult result, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有更新权限)");
        }
        if (result.hasErrors()) {
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String loginUser = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setId(id);
        exchangisJobVo.setModifyUser(loginUser);
        Message response = Message.ok();
        try {
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to update (没有更新任务权限)");
            }
            response.data("result", jobInfoService.updateJob(exchangisJobVo));
        } catch (Exception e) {
            String message = "Fail to update job: " + exchangisJobVo.getJobName() + " (更新任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(loginUser, TargetTypeEnum.JOB,exchangisJobVo.getId().toString(), "Job name is: " + exchangisJobVo.getJobName(), OperateTypeEnum.UPDATE,request);
        return response;
    }

    /**
     * Delete job
     *
     * @param id      id
     * @param request http request
     * @return message
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Message deleteJob(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to delete (没有删除权限)");
        }
        String loginUser = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok("job deleted");
        ExchangisJobVo jobVo = jobInfoService.getJob(id, true);
        try {
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to delete (没有删除任务权限)");
            }
            jobInfoService.deleteJob(id);
        } catch (Exception e) {
            String message = "Fail to delete job [ id: " + id + "] (删除任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(loginUser, TargetTypeEnum.JOB,id.toString().toString(), "Job name is: " + jobVo.getJobName(), OperateTypeEnum.UPDATE,request);
        return response;
    }


    /**
     * Get job
     *
     * @param request http request
     * @param id      id
     * @return message
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Message getJob(HttpServletRequest request, @PathVariable("id") Long id) {
        Message response = Message.ok();
        try {
            LOG.info("Request: {}", request);

            String loginUser = SecurityFilter.getLoginUsername(request);

            ExchangisJobVo job = jobInfoService.getJob(id, true);
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to get job (没有获取任务权限)");
            }
            job = jobInfoService.getDecoratedJob(request, id);
            response.data("result", job);
        } catch (Exception e) {
            String message = "Fail to get job detail (查询任务失败)";
            if (e.getCause() instanceof ExchangisJobServerException) {
                message += ", reason: " + e.getCause().getMessage();
            }
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Get all sub job list
     * @param request
     * @param projectId
     * @return
     */
//    @Deprecated
//    @RequestMapping(value = "/subJob/list", method = RequestMethod.GET)
//    public Message getSubJobList(HttpServletRequest request, @RequestParam(value = "projectId") Long projectId) {
//        Message response = Message.ok();
//        String loginUser = SecurityFilter.getLoginUsername(request);
//        try {
//            List<ExchangisJobVo> jobList = jobInfoService.getSubJobList(request, projectId);
//            if (!JobAuthorityUtils.hasAuthority(loginUser, projectId, OperationType.JOB_QUERY)) {
//                return Message.error("You have no permission to create Job (没有查询任务权限)");
//            }
//            response.data("result", jobList);
//        } catch (Exception e) {
//            String message = "Fail to get job detail (查询所有子任务列表失败)";
//            if (e.getCause() instanceof ExchangisJobServerException) {
//                message += ", reason: " + e.getCause().getMessage();
//            }
//            LOG.error(message, e);
//            response = Message.error(message);
//        }
//        return response;
//    }

    /**
     * Get job list
     *
     * @param projectId
     * @param jobName
     * @return
     */
    @RequestMapping(value = "/getJob/list", method = RequestMethod.GET)
    public Message getByNameWithProjectId(HttpServletRequest request,
                                          @RequestParam(value = "projectId") Long projectId,
                                          @RequestParam(value = "jobName", required = false) String jobName) {
        Message response = Message.ok();
        String loginUser = SecurityFilter.getLoginUsername(request);
        try {
            if (!JobAuthorityUtils.hasProjectAuthority(loginUser, projectId, OperationType.JOB_QUERY)) {
                return Message.error("You have no permission to create Job (没有查询任务权限)");
            }

            List<ExchangisJobVo> jobs = jobInfoService.getByNameWithProjectId(jobName, projectId);
            response.data("result", jobs);
        } catch (Exception e) {
            String message = "Fail to get job detail (查询任务失败)";
            if (e.getCause() instanceof ExchangisJobServerException) {
                message += ", reason: " + e.getCause().getMessage();
            }
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Save the job configuration
     *
     * @param id    id
     * @param jobVo job vo
     * @return message
     */
    @RequestMapping(value = "/{id}/config", method = RequestMethod.PUT)
    public Message saveJobConfig(@PathVariable("id") Long id,
                                 @RequestBody ExchangisJobVo jobVo, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to save content (没有保存任务权限)");
        }
        jobVo.setId(id);
        jobVo.setModifyUser(SecurityFilter.getLoginUsername(request));
        Message response = Message.ok();
        String loginUser = SecurityFilter.getLoginUsername(request);
        try {
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to update (没有更新任务权限)");
            }
            ExchangisJobVo exchangisJob = jobInfoService.updateJobConfig(jobVo);
            response.data("id", exchangisJob.getId());
        } catch (Exception e) {
            String message = "Fail to save the job configuration (保存任务配置失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(loginUser, TargetTypeEnum.JOB,id.toString(), "Job id is: " + id.toString(), OperateTypeEnum.UPDATE,request);
        return response;
    }

    @RequestMapping(value = "/{id}/content", method = RequestMethod.PUT)
    public Message saveSubJobs(@PathVariable("id") Long id,
                               @RequestBody ExchangisJobVo jobVo, HttpServletRequest request) {
        if (ExchangisLauncherConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to save content (没有保存任务权限)");
        }
        jobVo.setId(id);
        jobVo.setModifyUser(SecurityFilter.getLoginUsername(request));
        String loginUser = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok();
        try {
            if (!JobAuthorityUtils.hasJobAuthority(loginUser, id, OperationType.JOB_ALTER)) {
                return Message.error("You have no permission to save content (没有保存任务权限)");
            }
            ExchangisJobVo exchangisJob = jobInfoService.updateJobContent(jobVo);
            response.data("id", exchangisJob.getId());
        } catch (Exception e) {
            String message = "Fail to save the job content (保存任务内容失败)";
            if (e.getCause() instanceof ExchangisJobServerException
                    || e.getCause() instanceof ExchangisDataSourceException) {
                message += ", reason: " + e.getCause().getMessage();
            }
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(loginUser, TargetTypeEnum.JOB,id.toString(), "Job id is: " + id.toString(), OperateTypeEnum.UPDATE,request);
        return response;
    }
}
