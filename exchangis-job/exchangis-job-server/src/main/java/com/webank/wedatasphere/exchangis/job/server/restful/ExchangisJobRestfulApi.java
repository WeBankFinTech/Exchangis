package com.webank.wedatasphere.exchangis.job.server.restful;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
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
import java.util.*;


/**
 * The basic controller of Exchangis job
 */
@RestController
@RequestMapping(value = "dss/exchangis/job", produces = {"application/json;charset=utf-8"})
public class ExchangisJobRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobRestfulApi.class);

    /**
     * Job service
     */
    @Resource
    private JobInfoService jobInfoService;

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
        String userName = SecurityFilter.getLoginUsername(request);
        queryVo.setCreateUser(userName);
        try {
            PageResult<ExchangisJobVo> pageResult = jobInfoService.queryJobList(queryVo);
            return Message.ok().data("total", pageResult.getTotal()).data("result", pageResult.getList());
        } catch (Exception e) {
            LOG.error("Fail to query job list for user {}", userName, e);
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
        return Message.ok().data("result", new EngineTypeEnum[]{EngineTypeEnum.SQOOP});
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
            HttpServletRequest request) {
        if (result.hasErrors()) {
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setCreateUser(userName);
        Message response = Message.ok();
        try {
            response.data("result", jobInfoService.createJob(exchangisJobVo));
        } catch (Exception e) {
            String message = "Fail to create job: " + exchangisJobVo.getJobName() + " (创建任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
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
                           @RequestBody ExchangisJobVo exchangisJobVo) {
        return Message.error("Function will be supported in next version (该功能将在下版本支持)");
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
        if (result.hasErrors()) {
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = SecurityFilter.getLoginUsername(request);
        exchangisJobVo.setId(id);
        exchangisJobVo.setModifyUser(userName);
        Message response = Message.ok();
        try {
            if (!hasAuthority(userName, jobInfoService.getJob(id, true))) {
                return Message.error("You have no permission to update (没有更新权限)");
            }
            response.data("result", jobInfoService.updateJob(exchangisJobVo));
        } catch (Exception e) {
            String message = "Fail to update job: " + exchangisJobVo.getJobName() + " (更新任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
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
        String userName = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok("job deleted");
        try {
            if (!hasAuthority(userName, jobInfoService.getJob(id, true))) {
                return Message.error("You have no permission to update (没有删除权限)");
            }
            jobInfoService.deleteJob(id);
        } catch (Exception e) {
            String message = "Fail to delete job [ id: " + id + "] (删除任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
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
            ExchangisJobVo job = jobInfoService.getDecoratedJob(request, id);
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
     * Save the job configuration
     *
     * @param id    id
     * @param jobVo job vo
     * @return message
     */
    @RequestMapping(value = "/{id}/config", method = RequestMethod.PUT)
    public Message saveJobConfig(@PathVariable("id") Long id,
                                 @RequestBody ExchangisJobVo jobVo, HttpServletRequest request) {
        jobVo.setId(id);
        jobVo.setModifyUser(SecurityFilter.getLoginUsername(request));
        Message response = Message.ok();
        try {
            ExchangisJobVo exchangisJob = jobInfoService.updateJobConfig(jobVo);
            response.data("id", exchangisJob.getId());
        } catch (Exception e) {
            String message = "Fail to save the job configuration (保存任务配置失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    @RequestMapping(value = "/{id}/content", method = RequestMethod.PUT)
    public Message saveSubJobs(@PathVariable("id") Long id,
                               @RequestBody ExchangisJobVo jobVo, HttpServletRequest request) {
        jobVo.setId(id);
        jobVo.setModifyUser(SecurityFilter.getLoginUsername(request));
        Message response = Message.ok();
        try {
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
        return response;
    }

    /**
     * TODO complete the authority strategy
     *
     * @param username username
     * @param job      job
     * @return
     */
    private boolean hasAuthority(String username, ExchangisJobVo job) {
        return Objects.nonNull(job) && username.equals(job.getCreateUser());
    }

}
