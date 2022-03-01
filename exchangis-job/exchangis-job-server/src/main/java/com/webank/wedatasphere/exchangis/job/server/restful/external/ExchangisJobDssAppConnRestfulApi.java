package com.webank.wedatasphere.exchangis.job.server.restful.external;

import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.service.impl.DefaultJobExecuteService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import org.apache.commons.lang.StringUtils;
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
import java.util.Objects;

/**
 * Define to support the app conn, in order to distinguish from the inner api
 */
@RestController
@RequestMapping(value = "/exchangis/dss/job", produces = {"application/json;charset=utf-8"})
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
    @RequestMapping( value = "", method = RequestMethod.POST)
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
        try{
            response.data("result", jobInfoService.createJob(exchangisJobVo));
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
    @RequestMapping( value = "/{id:\\d+}", method = RequestMethod.DELETE)
    public Message deleteJob(@PathVariable("id") Long id, HttpServletRequest request) {
        String userName = SecurityFilter.getLoginUsername(request);
        Message response = Message.ok("dss job deleted");
        try {
            if (!hasAuthority(userName, jobInfoService.getJob(id, true))){
                return Message.error("You have no permission to update (没有删除权限)");
            }
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
            if (!hasAuthority(userName, jobInfoService.getJob(id , true))){
                return Message.error("You have no permission to update (没有更新权限)");
            }
            response.data("result", jobInfoService.updateJob(exchangisJobVo));
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
    public Message executeJob(@PathVariable("id") Long id, HttpServletRequest request) {
        String loginUser = SecurityFilter.getLoginUsername(request);
        Message result = Message.ok();
        ExchangisJobInfo jobInfo = null;
        try {
            // First to find the job from the old table.
            ExchangisJobVo jobVo = jobInfoService.getJob(id, false);
            if (Objects.isNull(jobVo)){
                return Message.error("Job related the id: [" + id + "] is Empty(关联的DSS任务不存在)");
            }
            // Convert to the job info
            jobInfo = new ExchangisJobInfo(jobVo);
            if (!hasAuthority(loginUser, jobVo)){
                return Message.error("You have no permission to execute job (没有执行DSS任务权限)");
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
        return result;
    }

    public Message importJob(){
        return null;
    }

    public Message exportJob(){
        return null;
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
}
