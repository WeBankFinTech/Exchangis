/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.controller;

import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.auth.annotations.ContainerAPI;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.DateTool;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.exec.remote.ExecuteService;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.alarm.AlarmTemplate;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.job.domain.JobTask;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import com.webank.wedatasphere.exchangis.job.query.JobLogQuery;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.job.service.impl.JobInfoServiceImpl;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by devendeng on 2018/8/24.
 * @author devendeng
 */
@RestController
@RequestMapping("/api/v1/jobtask")
public class JobTaskController extends AbstractGenericController<JobTask, JobLogQuery> {
    private static final Logger LOG = LoggerFactory.getLogger(JobTaskController.class);
    private static final String ID_LIST_PARAM = "idList";
    @Resource
    private JobTaskService jobTaskService;
    @Resource
    private JobInfoServiceImpl jobInfoService;
    @Resource
    private ExecuteService executeService;

    @Override
    public IBaseService<JobTask> getBaseService() {
        return jobTaskService;
    }
    @Resource
    private GroupService groupService;

    @Resource
    private UserInfoService userInfoService;

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(JobTask.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            }
            return groupService.queryGroupRefProjectsByUser(userName);
        });
        security.registerExternalDataAuthGetter(JobTask.class, jobTask -> {
            if(null == jobTask){
                return Collections.emptyList();
            }
            return Collections.singletonList(String.valueOf(jobTask.getProjectId()));
        });
        security.registerExternalDataAuthScopeGetter(JobTask.class, jobTask -> Collections.singletonList(DataAuthScope.ALL));
    }

    @Override
    public Response<Object> pageList(JobLogQuery pageQuery, HttpServletRequest request) {
        int pageSize = pageQuery.getPageSize();
        if(pageSize == 0){
            pageQuery.setPageSize(10);
        }
        String username = security.getUserName(request);
        security.bindUserInfoAndDataAuth(pageQuery, request,
                security.userExternalDataAuthGetter(getActualType()).get(username));
        PageList<JobTask> list = getBaseService().findPage(pageQuery);
        List<JobTask> dataList = list.getData();
        if(null != dataList && !dataList.isEmpty()){
            dataList.forEach( task ->{
                Map<String, Object> parameters = jobTaskService.getTaskParameters(task.getId());
                Object paramDate = parameters.get(DefaultParams.Task.PARAM_DATE);
                if(null != paramDate){
                    SimpleDateFormat format = new SimpleDateFormat(DateTool.TIME_PLACEHOLDER_DATE_TIME
                            .substring(2, DateTool.TIME_PLACEHOLDER_DATE_TIME.length()-1));
                    parameters.put(DefaultParams.Task.PARAM_DATE, format
                            .format(new Date(Long.parseLong(String.valueOf(paramDate)))));
                }
                task.setTaskParams(parameters);

            });
        }
        return new Response<>().successResponse(list);
    }

    /**
     * Notify that the job has been completed
     * @param taskId
     * @param status
     * @return
     */
    @ContainerAPI
    @RequestMapping(value = "/notifyJobComplete",method = RequestMethod.POST)
    public Response<String> notifyTaskComplete(@RequestParam long taskId, @RequestParam ExecuteStatus status,
                                               @RequestParam String address,
                                               @RequestParam String message, HttpServletRequest request){
        LOG.info("Task run complete.taskId [{}],status [{}],message {} ",taskId,status,message);
        Calendar calendar = Calendar.getInstance();
        JobTask jobTask = jobTaskService.get(taskId);
        if(StringUtils.isBlank(jobTask.getExecuterAddress())){
            jobTask.setExecuterAddress(address.replace("http://", ""));
        }
        jobTask.setCompleteTime(calendar.getTime());
        jobTask.setStatus(status.name());
        jobTask.setExecuteMsg(message);
        jobTask.setStateSpeed(null);
        if(!jobTaskService.selectAndUpdate(jobTask)){
            LOG.error("Update task: [{}] status [{}] failed", taskId, status.name());
            return new Response<String>().errorResponse(CodeConstant.SYS_ERROR, null ,
                    super.informationSwitch("exchange.job_task.update.task.failed") + status.name() +super.informationSwitch("exchange.job_task.task.status.failed"));
        }
        JobInfo jobInfo=jobInfoService.get(jobTask.getJobId());
        if(null != jobInfo) {
            if (status == ExecuteStatus.SUCCESS) {
                jobInfo.setLastTriggerTime(jobTask.getTriggerTime());
            }
            jobInfoService.update(jobInfo);
        }
        //Remove element related with task in queue, but retain task info
        jobTaskService.removeFromQueue(taskId);
        if(status == ExecuteStatus.FAILD){
            //Send alarm information
            if(jobInfo == null){
                jobInfo = new JobInfo();
                jobInfo.setAlarmUser(jobTask.getJobAlarmUser());
                jobInfo.setCreateUser(jobTask.getJobCreateUser());
            }
            jobInfoService.sendInfoToAlarm(taskId, jobInfo, AlarmTemplate.TITLE_TASK_RUN_FAID,
                    PatternInjectUtils.inject(AlarmTemplate.TASK_RUN_FAID,
                            new Object[]{jobTask.getJobName(), jobTask.getJobId(), taskId}, false, false,true));
            if("scheduler".equals(jobTask.getTriggerType())){
                //Ignore schedulerRetry(jobInfo, jobTask)
            }
        }
        return new Response<String>().successResponse(super.informationSwitch("exchange.job_task.notify.task.success"));
    }

    /**
     * Notify timeout
     * @param taskId
     * @param message
     * @return
     */
    @ContainerAPI
    @RequestMapping(value = "/notifyTaskTimeout", method = RequestMethod.POST)
    public Response<String> notifyTaskTimeout(@RequestParam long taskId, @RequestParam String message){
        LOG.info("Task run timeout. taskId [{}], message {} ", taskId, message);
        JobTask jobTask = jobTaskService.get(taskId);
        if(!jobTask.getStatus().equals(ExecuteStatus.SUCCESS.name())){
            jobTask.setStatus(ExecuteStatus.RUNNING_TIMEOUT.name());
            jobTask.setExecuteMsg(message);
            LOG.info("Update task [{}] status to RUNNING_TIMEOUT", taskId);
            if(jobTaskService.update(jobTask)){
                LOG.info("Send task [{}] timeout message to IMS", taskId);
                JobInfo jobInfo = jobInfoService.get(jobTask.getJobId());
                jobInfoService.sendInfoToAlarm(taskId, jobInfo, AlarmTemplate.TITLE_TASK_RUN_TIMEOUT,
                        PatternInjectUtils.inject(AlarmTemplate.TASK_RUN_TIMEOUT,
                                new Object[]{jobInfo.getJobName(), jobInfo.getId(), taskId, jobInfo.getTimeout(), message},
                                false, false, true));
            }
        }
        return new Response<String>().successResponse("notify success");
    }

    /**
     * Status list
     * @param reqMap
     * @return
     */
    @RequestMapping(value = "/status/list", method = RequestMethod.PUT)
    public Response<List<String>> statusList(@RequestBody Map<String, List<Long>> reqMap){
        List<String> statusList = new ArrayList<>();
        List<Long> idList = reqMap.get(ID_LIST_PARAM);
        if(null != idList && idList.size() > 0){
            statusList = jobTaskService.statusList(idList);
        }
        return new Response<List<String>>().successResponse(statusList);
    }
    /**
     * Get task log
     * @param taskId task id
     * @param startLine start line
     * @return
     */
    @RequestMapping(value = "/log",method = RequestMethod.GET)
    public Response<LogResult> log(@RequestParam("taskId") long taskId, @RequestParam("startLine") int startLine,
                                   @RequestParam("windSize")int windSize, HttpServletRequest request){
        JobTask jobTask = jobTaskService.get(taskId);
        if(jobTask != null){
            JobInfo jobInfo = jobInfoService.get(jobTask.getJobId());
            if(null != jobInfo) {
                if (!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)) {
                    return new Response<LogResult>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_task.not.access.rights"));
                }
            }
            Response<LogResult> log = null;
            if(jobTask.getExecuterAddress() != null){
                try {
                    log = executeService.log(jobTask.getExecuterAddress(), jobTask.getJobId(), jobTask.getId(), startLine, windSize);
                }catch(Exception e){
                    LOG.error(e.getMessage(), e);
                    return new Response<LogResult>().errorResponse(CodeConstant.SYS_ERROR,  new LogResult(startLine, 0, super.informationSwitch("exchange.job_task.node.be.down"), true), super.informationSwitch("exchange.job_task.node.be.lost"));
                }
            }else{
                String logContent = "";
                if(!StringUtils.isBlank(jobTask.getTriggerMsg())){
                    logContent += jobTask.getTriggerMsg() + "\n";
                }
                if(!StringUtils.isBlank(jobTask.getExecuteMsg())){
                    logContent += jobTask.getExecuteMsg() + "\n";
                }
                if(StringUtils.isBlank(logContent)){
                    logContent = "No Logs are available";
                }
                LogResult logResult = new LogResult(startLine, 0, logContent , true);
                return new Response<LogResult>().successResponse(logResult);
            }
            ExecuteStatus[] endStatus = new ExecuteStatus[]{
                    ExecuteStatus.FAILD,
                    ExecuteStatus.KILL,
                    ExecuteStatus.SUCCESS
            };
            for(ExecuteStatus status : endStatus){
                if(jobTask.getStatus().equals(status.name()) && log.getData() != null){
                    log.getData().setEnd(true);
                    break;
                }
            }
            return log;
        }else{
            LogResult log = new LogResult(startLine, 0, "task not exists", true);
            return new Response<LogResult>().errorResponse(CodeConstant.TASK_NOT_EXISTS,log,taskId + super.informationSwitch("exchange.job_task.tasks.not.exist"));
        }
    }

    /**
     * Kill task,use Feign Client to do request to remote server
     * @param taskId task id
     * @return
     */
    @RequestMapping(value = "/kill/{taskId:\\w+}",method = RequestMethod.POST)
    public Response<String> kill(@PathVariable Long taskId, HttpServletRequest request){
        JobTask jobTask = jobTaskService.get(taskId);
        if(jobTask != null){
            JobInfo jobInfo = jobInfoService.get(jobTask.getJobId());
            if(null != jobInfo) {
                if (!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)) {
                    return new Response<String>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_task.not.access.rights"));
                }
            }
            Response<String> rsp = null;
            if(jobTask.getStatus().equalsIgnoreCase(ExecuteStatus.RUNNING.name()) ||
                    jobTask.getStatus().equals(ExecuteStatus.RUNNING_TIMEOUT.name())){
                try {
                    rsp = executeService.kill(jobTask.getExecuterAddress(), jobTask.getJobId(), jobTask.getId());
                } catch(Exception e){
                    LOG.error(e.getMessage(), e);
                    return new Response<String>().errorResponse(CodeConstant.SYS_ERROR,  null, super.informationSwitch("exchange.job_task.node.be.lost"));
                }
                if(rsp.getCode() == 0){
                    //Update db status
                    jobTask.setStatus(ExecuteStatus.KILL.name());
                    jobTask.setStateSpeed(null);
                    jobTaskService.update(jobTask);
                    jobTaskService.removeFromQueue(taskId);
                }
            }else{
                rsp = new Response<>();
                rsp.setMessage("Task status is ["+ jobTask.getStatus() + "],don't kill");
                rsp.setData(null);
                rsp.setCode(-1);
            }
            return rsp;
        }else{
            return new Response<String>().errorResponse(CodeConstant.TASK_NOT_EXISTS,null,super.informationSwitch("exchange.job_task.task.not.exist"));
        }
    }

    @Override
    public Response<JobTask> show(@PathVariable Long id, HttpServletRequest request) throws Exception {
        JobTask log = jobTaskService.get(id);
        if(null != log && !hasDataAuth(JobTask.class, DataAuthScope.READ, request, log)){
            return new Response<JobTask>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_task.not.access.rights"));
        }
        return new Response<JobTask>().successResponse(log);
    }

    @Override
    public Response<JobTask> delete(@PathVariable Long id, HttpServletRequest request) {
        JobTask jobTask= jobTaskService.get(id);
        if(null != jobTask ){
            JobInfo jobInfo = jobInfoService.get(jobTask.getJobId());
            if(null != jobInfo) {
                if (!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)) {
                    return new Response<JobTask>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_task.not.access.rights"));
                }
            }
            boolean canDelete = true;
            if(null == jobTask.getStatus()){
                canDelete = false;
            }else{
                ExecuteStatus status = ExecuteStatus.valueOf(jobTask.getStatus());
                if(status == ExecuteStatus.RUNNING || status == ExecuteStatus.RUNNING_TIMEOUT
                        || status == ExecuteStatus.COMMIT){
                    canDelete = false;
                }
            }
            if(!canDelete){
                return new Response<JobTask>().errorResponse(CodeConstant.PARAMETER_ERROR,
                        null, super.informationSwitch("exchange.job_task.cannot.be.deleted"));
            }
        }
        Response<JobTask> result =  super.delete(id, request);
        jobTaskService.removeFromQueue(id);
        return result;
    }

    @RequestMapping(value = "/{taskId:\\w+}/speed/limit/{speedLimit:\\w+}", method = RequestMethod.PUT)
    public Response<Object> speedLimit(@PathVariable("taskId")long taskId,
                                       @PathVariable("speedLimit") int speedLimit, HttpServletRequest request){
        JobTask jobTask = jobTaskService.get(taskId);
        if(null == jobTask){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR,
                    null, super.informationSwitch("exchange.job_task.tasks.not.exist"));
        }
        if(!hasDataAuth(JobTask.class, DataAuthScope.WRITE, request, jobTask)){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null,
                    super.informationSwitch("exchange.job_task.not.access.rights"));
        }
        ExecuteStatus[] runStatus = new ExecuteStatus[]{
                ExecuteStatus.RUNNING, ExecuteStatus.RUNNING_TIMEOUT
        };
        for(ExecuteStatus status : runStatus){
            if(jobTask.getStatus().equals(status.name())){
                try {
                    this.jobTaskService.limitSpeed(jobTask, speedLimit);
                }catch(Exception e){
                    LOG.error(e.getMessage(), e);
//                    return new Response<>().errorResponse(CodeConstant.SYS_ERROR,  null, super.informationSwitch("udes.jobtask.node.be.lost"));
                }
                break;
            }
        }
        return new Response<>().successResponse("success");
    }

    private void schedulerRetry(JobInfo jobInfo, JobTask jobTask){
        int runtimes = jobTask.getRunTimes();
        if(jobInfo.getFailReteryCount() > 0){
            if(jobInfo.getFailReteryCount() <= jobTask.getRunTimes()){
                LOG.info("Scheduler type task run failed, begin to retry.jobId [{}]", jobInfo.getId());
                jobTask.setRunTimes(jobTask.getRunTimes() + 1);
                jobTask.setCompleteTime(null);
                jobTask.setTriggerTime(Calendar.getInstance().getTime());
                jobTask.setExecuteMsg("");
                jobTask.setExecuterAddress("");
                jobTask.setStatus(ExecuteStatus.COMMIT.name());
                jobTaskService.insertTaskAndAddToQueue(jobTask);
            }else{
                LOG.info("Job [{}]'s scheduler task [{}] run time [{}] > retry count [{}], will not retry", jobTask.getJobId(),
                        jobTask.getId(), runtimes,jobInfo.getFailReteryCount());
            }
        }else{
            LOG.info("Job [{}]'s scheduler task [{}] fail retry count down to 0",jobInfo.getId(), jobTask.getId());
        }
    }

}
