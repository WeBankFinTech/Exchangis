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
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.datasource.Configuration;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUser;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.exec.service.ExecUserService;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.job.domain.*;
import com.webank.wedatasphere.exchangis.job.service.JobFuncService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.config.HistoryInterval;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import com.webank.wedatasphere.exchangis.job.query.JobInfoQuery;
import com.webank.wedatasphere.exchangis.job.service.JobInfoConfService;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.job.service.impl.JobInfoServiceImpl;
import com.webank.wedatasphere.exchangis.project.domain.Project;
import com.webank.wedatasphere.exchangis.project.service.impl.ProjectServiceImpl;
import com.webank.wedatasphere.exchangis.scheduler.cluster.bean.TaskUtil;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Job management
 * @author Created by devendeng on 2018/8/24.
 */
@RestController
@RequestMapping("/api/v1/jobinfo")
public class JobInfoController extends AbstractGenericController<JobInfo, JobInfoQuery> {

    private static final Logger logger = LoggerFactory.getLogger(JobInfoController.class);
    @Resource
    private JobInfoServiceImpl jobInfoService;

    @Resource
    private DataSourceServiceImpl dataSourceService;

    @Resource
    private JobInfoConfService jobInfoConfService;

    @Resource
    private JobTaskService jobTaskService;

    @Resource
    private ProjectServiceImpl projectService;

    @Resource
    private ExecNodeInfoService execNodeInfoService;

    @Resource
    private HistoryInterval historyInterval;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ExecUserService execUserService;

    @Resource
    private Configuration conf;

    @Override
    public IBaseService<JobInfo> getBaseService() {
        return jobInfoService;
    }
    @Resource
    private GroupService groupService;

    @Resource
    private JobFuncService jobFuncService;

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(JobInfo.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            }
            return groupService.queryGroupRefProjectsByUser(userName);
        });
        security.registerExternalDataAuthGetter(JobInfo.class, jobInfo -> {
            if(null == jobInfo){
                return Collections.emptyList();
            }
            return Collections.singletonList(String.valueOf(jobInfo.getProjectId()));
        });
        security.registerExternalDataAuthScopeGetter(JobInfo.class, jobInfo -> Collections.singletonList(DataAuthScope.ALL));
    }

    @RequestMapping(value = "/func/{tabName:\\w+}/{funcType:\\w+}", method = RequestMethod.GET)
    public Response<Object> jobFuncList(@PathVariable("tabName")String tabName,
                                        @PathVariable("funcType")String funcType,
                                        HttpServletResponse response){
        try {
            //Limit that the tab should be an engine tab
            JobEngine.valueOf(tabName.toUpperCase());
            funcType = funcType.toUpperCase();
            JobFunction.FunctionType funcTypeEnum = JobFunction.FunctionType.valueOf(funcType);
            List<JobFunction> functionList = jobFuncService.getFunctions(tabName, funcTypeEnum);
            return new Response<>().successResponse(functionList);
        }catch(IllegalArgumentException e){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new Response<>().errorResponse(-1, null, "");
        }
    }

    @RequestMapping(value = "/func/{funcType:\\w+}", method = RequestMethod.GET)
    public Response<Object> jobFuncList(@PathVariable("funcType")String funcType, HttpServletResponse response){
        return jobFuncList(JobEngine.DATAX.name(), funcType, response);
    }
    /**
     * Handle exception of job parameters
     * @param e
     * @return
     */
    @ExceptionHandler(value = JobDataParamsInValidException.class)
    public Response<Object> errorParameterHandler(JobDataParamsInValidException e){
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, e.getMessage());
    }

    /**
     * Trigger job
     * @param jobid job id
     * @param request request
     * @return
     */
    public Response<String> triggerJob(@PathVariable(name = "id") Long jobid, HttpServletRequest request){
        if(!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfoService.get(jobid))){
            return new Response<String>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        boolean result = jobInfoService.triggerJob(jobid);
        return result ? new Response<String>().successResponse(null) :
                new Response<String>().errorResponse(1, null, super.informationSwitch("udes.jobinfo.error.task.failed"));
    }


    @Override
    public Response<JobInfo> add(@Valid @RequestBody JobInfo jobInfo, HttpServletRequest request) {
        //Check data source permission
        hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, request);
        hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, request);
        Project project = projectService.get(jobInfo.getProjectId());
        if(null == project){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.project.notExist"));
        }else if(!hasDataAuth(Project.class, DataAuthScope.WRITE, request, project)){
            return new Response<JobInfo>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.project.notAuth"), project.getProjectName());
        }
        security.bindUserInfo(jobInfo, request);
        jobInfo.setJobName(StringEscapeUtils.escapeHtml3(jobInfo.getJobName()));
        if(StringUtils.isNotBlank(jobInfo.getCreateUser())
                && isDuplicate(jobInfo.getJobName(), jobInfo.getCreateUser())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null ,super.informationSwitch("exchange.job_info.error.name.repeat"));
        }
        String username = security.getUserName(request);
        if(StringUtils.isBlank(jobInfo.getExecUser())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.users.empty"));
        }else if(jobInfo.getExecUser().equals(username)){
            //If executive user equals to userName, try to create automatically
            userInfoService.bindExecUser(username, jobInfo.getExecUser());
        }else {
            //Check if the user has the permission of this execution user
            if (!execUserService.havePermission(username, jobInfo.getExecUser())) {
                return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.not.executable"));
            }
        }
        if(!jobInfo.getExecNodes().isEmpty() && !execNodeInfoService.haveNodeIdsPermission(username, jobInfo.getExecNodes())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.execute.failed"));
        }
        //Set not disposable job
        jobInfo.setDisposable(false);
        //Escape
        jobInfo.setAlarmUser(StringEscapeUtils.escapeHtml3(jobInfo.getAlarmUser()));
        jobInfo.setJobDesc(StringEscapeUtils.escapeHtml3(jobInfo.getJobDesc()));
        boolean result = jobInfoConfService.add(jobInfo);
        return result ? new Response<JobInfo>().successResponse(jobInfo) :
                new Response<JobInfo>().errorResponse(1, null, super.informationSwitch("exchange.job_info.error.add.failed"));
    }

    /**
     * Provide api to execute job with 执行任务接口
     * data source configuration, authentication file and so on
     * @param jobFile
     * @param srcAuthFile
     * @param dstAuthFile
     * @param request
     * @return
     */
    @RequestMapping(value = "/run", method = RequestMethod.POST, headers = {"content-type=multipart/form-data"})
    public Response<Long> run(@RequestParam(value = "job")MultipartFile jobFile,
                                 @RequestParam(value = "srcAuthFile", required = false)MultipartFile srcAuthFile,
                                 @RequestParam(value = "dstAuthFile", required = false)MultipartFile dstAuthFile,
                                 HttpServletRequest request){
        //Get userName from request cookie
        String userName = security.getUserName(request);
        File[] files = new File[2];
        long taskId;
        try {
            logger.info("Parsing job file: " + jobFile.getOriginalFilename() + ", userName: " + userName);
            JobInfo jobInfo = Json.fromJson(jobFile.getInputStream(), JobInfo.class);
            if(StringUtils.isBlank(jobInfo.getExecUser())){
                return new Response<Long>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.users.empty"));
            }else if(jobInfo.getExecUser().equals(userName)){
                //If executive user equals to userName, try to create automatically
                userInfoService.bindExecUser(userName, jobInfo.getExecUser());
            }else{
                //Check if the user has the permission of this executive user
                if(!execUserService.havePermission(userName,jobInfo.getExecUser())){
                    return new Response<Long>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.not.executable"));
                }
            }
            //Check data source permission
            hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, request);
            hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, request);
            jobInfo.setJobName("script"+System.currentTimeMillis() + "_" + AppUtil.getIpAndPort().substring("http://".length())
                    + "_" + Thread.currentThread().getId());
            List<String> execNodeNames = jobInfo.getExecNodeNames();
            if(!execNodeNames.isEmpty()){
                Pair<List<ExecutorNode>, Boolean> resultPair = execNodeInfoService.haveNodeNamesPermission(security.getUserName(request), execNodeNames);
                if(!resultPair.getRight()){
                    return new Response<Long>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.execute.failed"));
                }
                jobInfo.setExecNodes(resultPair.getLeft().stream().map(ExecutorNode::getId).collect(Collectors.toList()));
            }
            Map<String, Object> dataSrcParams = jobInfo.getConfig().getDataSrcParams();
            Map<String, Object> dataDstParams = jobInfo.getConfig().getDataDstParams();
            // Add data source model params to job params
            this.addDataSourceModelParamsToJobParams(dataSrcParams);
            this.addDataSourceModelParamsToJobParams(dataDstParams);
            // Persist auth file and add file information to job params
            files[0] = this.persistDisAuthFile(dataSrcParams, srcAuthFile);
            files[1] = this.persistDisAuthFile(dataDstParams, dstAuthFile);
            security.bindUserInfo(jobInfo, request);
            jobInfo.setAttachFiles(files);
            //Job created dynamically is disposable job
            jobInfo.setDisposable(true);
            //If has no executor node
            if(jobInfo.getExecNodes().isEmpty() && jobInfo.getExecNodeNames().isEmpty()){
                return new Response<Long>().errorResponse(CodeConstant.SYS_ERROR, null, super.informationSwitch("exchange.job_info.executor.node.notExists"));
            }
            //Before running task, bind and relate executive user and nodes
            bindAndRelateExecNodesAndUser(jobInfo.getExecNodes(), jobInfo.getExecNodeNames(), jobInfo.getExecUser());
            taskId = jobInfoConfService.runJob(jobInfo);
       }catch (Exception e) {
            logger.error("Exception happened while parsing job File: " + jobFile.getOriginalFilename() + ", username: " + userName, e);
            for(File duplicateFile : files){
                if(null != duplicateFile){
                    logger.info("Delete duplicate file: " + duplicateFile.getName());
                    if(!duplicateFile.delete()){
                        logger.error("IO_ERROR:Cannot delete duplicate file: " + duplicateFile.getName() +
                                ", please check the permission, path: " + duplicateFile.getPath());
                    }
                }
            }
            if (e instanceof EndPointException){
                throw (EndPointException)e;
            }else if(e instanceof JobDataParamsInValidException){
                throw (JobDataParamsInValidException)e;
            }
           return new Response<Long>().errorResponse(CodeConstant.EXECUTER_ERROR, null, super.informationSwitch("exchange.job_info.error.execute.exception"));
        }
        return new Response<Long>().successResponse(taskId);
    }
    /**
     * Provide api to execute by jobId
     * @param jobId job id
     * @return
     */
    @RequestMapping(value = "/run/{id}",method = RequestMethod.GET)
    public Response<Long> run(@PathVariable(name = "id") Integer jobId, @RequestParam String userName,
                                 HttpServletRequest request){
        JobInfo jobInfo = jobInfoService.get(jobId);
        if(!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)){
            return new Response<Long>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        //Check data source permission
        hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, request);
        hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, request);
        String userName0 = security.getUserName(request);
        //Generate job task
        JobTask jobTask = new JobTask(jobInfo,  "api",  userName0);
        //Assemble job task's parameters
        List<JobTaskParams> paramsList = new ArrayList<>();
        paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_DATE, String.valueOf(System.currentTimeMillis())));
        if(jobInfo.getSync() == JobSyncEnum.INCR) {
            paramsList.addAll(TaskUtil.generateIncrParams(jobInfo, jobTask));
        }
        //If has no executor node
        if(jobInfo.getExecNodes().isEmpty() && jobInfo.getExecNodeNames().isEmpty()){
            return new Response<Long>().errorResponse(CodeConstant.SYS_ERROR, null, super.informationSwitch("exchange.job_info.executor.node.notExists"));
        }
//        Before running task, bind and relate executive user and nodes
        bindAndRelateExecNodesAndUser(jobInfo.getExecNodes(), jobInfo.getExecNodeNames(), jobInfo.getExecUser());
        jobTaskService.insertTaskAndAddToQueue(jobTask, paramsList);
        if(jobTask.getStatus().equals(ExecuteStatus.FAILD.name())){
            return new Response<Long>().errorResponse(CodeConstant.EXECUTER_ERROR, jobTask.getId(), jobTask.getExecuteMsg());
        }else{
            return new Response<Long>().successResponse(jobTask.getId());
        }
    }

    /**
     * Update operation
     * @param jobInfo
     * @param request
     * @return
     */
    @Override
    public Response<JobInfo> update(@Valid @RequestBody JobInfo jobInfo, HttpServletRequest request) {
        if(jobInfo.getId() == null){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.jobid.empty"));
        }
        JobInfo oldOne = jobInfoService.get(jobInfo.getId());
        if(null == oldOne){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("udes.jobinfo.error.taskid.invalid"));
        }
        if(!hasDataAuth(JobInfo.class, DataAuthScope.WRITE, request, oldOne)){
            return new Response<JobInfo>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        //Check data source permission
        hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, request);
        hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, request);
        Project project = projectService.get(jobInfo.getProjectId());
        if(null == project){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.project.notExist"));
        }
        if(jobInfo.getExecNodes().size() <= 0){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.nodes.empty"));
        }
        security.bindUserInfo(jobInfo, request);
        jobInfo.setJobName(StringEscapeUtils.escapeHtml3(jobInfo.getJobName()));
        if(StringUtils.isNotBlank(jobInfo.getCreateUser())
                && !jobInfo.getJobName().toLowerCase().equals(oldOne.getJobName().toLowerCase())
                && isDuplicate(jobInfo.getJobName(), jobInfo.getCreateUser())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null ,super.informationSwitch("exchange.job_info.error.name.repeat"));
        }
        String userName = security.getUserName(request);
        if(StringUtils.isBlank(jobInfo.getExecUser())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.users.empty"));
        }else if(jobInfo.getExecUser().equals(userName)){
            //If executive user equals to userName, try to create automatically
            userInfoService.bindExecUser(userName, jobInfo.getExecUser());
        }else{
            if(!execUserService.havePermission(userName, jobInfo.getExecUser())){
                return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,super.informationSwitch("exchange.job_info.error.not.executable"));
            }
        }
        if(!jobInfo.getExecNodes().isEmpty() && !execNodeInfoService.haveNodeIdsPermission(userName, jobInfo.getExecNodes())){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.execute.failed"));
        }
        //Escape
        jobInfo.setAlarmUser(StringEscapeUtils.escapeHtml3(jobInfo.getAlarmUser()));
        jobInfo.setJobDesc(StringEscapeUtils.escapeHtml3(jobInfo.getJobDesc()));
        jobInfo.setLastTriggerTime(oldOne.getLastTriggerTime());
        jobInfo.setModifyTime(oldOne.getModifyTime());
        boolean result = jobInfoConfService.update(jobInfo);
        return result ? new Response<JobInfo>().successResponse(null) :
                new Response<JobInfo>().errorResponse(1, null, super.informationSwitch("exchange.job_info.error.update.failed"));
    }

    @Override
    public Response<JobInfo> show(@PathVariable Long id, HttpServletRequest request) throws Exception {
        //Config recover
        JobInfo jobInfo = jobInfoConfService.get(Math.toIntExact(id));
        if(!hasDataAuth(JobInfo.class, DataAuthScope.READ, request, jobInfo)){
            return new Response<JobInfo>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        return new Response<JobInfo>().successResponse(jobInfo);
    }

    @Override
    public Response<JobInfo> delete(@PathVariable Long id, HttpServletRequest request) {
        if(!hasDataAuth(JobInfo.class,DataAuthScope.WRITE, request, jobInfoService.get(id))){
            return new Response<JobInfo>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        return super.delete(id, request);
    }

    @RequestMapping(value = "/history/run", method = RequestMethod.POST)
    public Response<Object> historyRuns(@RequestBody @Valid JobHistoryReq jobHistoryReq,HttpServletRequest request){
        if(jobHistoryReq.getStartTime() == 0 || jobHistoryReq.getEndTime() == 0 ||
                jobHistoryReq.getStartTime() - jobHistoryReq.getEndTime() > 0){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.time.range"));
        }
        JobInfo jobInfo = jobInfoService.get(jobHistoryReq.getJobId());
        if(!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        //Check data source permission
        hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, request);
        hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, request);
        if(jobInfo.getSync() == JobSyncEnum.INCR){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.jobs.notrun"));
        }
        boolean havePlaceholder = historyInterval.checkTime(jobInfo,jobHistoryReq);
        if(!havePlaceholder){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.date.placeholder"));
        }
        boolean checkStep = historyInterval.checkStep(jobHistoryReq);
        if(!checkStep){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_info.error.number.large"));
        }
        String userName = security.getUserName(request);
        jobTaskService.runHistoryTaskByTime(jobInfo,jobHistoryReq,userName);
        return new Response<>().successResponse(super.informationSwitch("exchange.job_info.return.success.info"));
    }

    @RequestMapping(value="/{id:\\w+}/sched/pause", method = RequestMethod.PUT)
    public Response<Object> schedulerPause(@PathVariable("id")Long jobId, HttpServletRequest request){
        JobInfo jobInfo = jobInfoService.get(jobId);
        if(!hasDataAuth(JobInfo.class, DataAuthScope.WRITE, request, jobInfo)){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        if(StringUtils.isNotBlank(jobInfo.getJobStatus())){
            JobSchedulerStatusEnum statusEnum = JobSchedulerStatusEnum.valueOf(jobInfo.getJobStatus());
            if(statusEnum == JobSchedulerStatusEnum.BLOCKED || statusEnum == JobSchedulerStatusEnum.NORMAL){
                boolean result = jobInfoService.pause(jobId);
                if(!result){
                    return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, super.informationSwitch("udes.jobinfo.error.task.start"));
                }
            }else{
                return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("udes.jobinfo.error.unconventional.task"));
            }
        }
        return new Response<>().successResponse(super.informationSwitch("exchange.job_info.return.success.info"));
    }

    @RequestMapping(value = "/{id:\\w+}/sched/start", method = RequestMethod.PUT)
    public Response<Object> schedulerStart(@PathVariable("id")Long jobId, HttpServletRequest request){
        JobInfo jobInfo = jobInfoService.get(jobId);
        if(!hasDataAuth(JobInfo.class, DataAuthScope.EXECUTE, request, jobInfo)){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.job_info.error.access.rights"));
        }
        if(StringUtils.isNotBlank(jobInfo.getJobStatus())){
            JobSchedulerStatusEnum statusEnum = JobSchedulerStatusEnum.valueOf(jobInfo.getJobStatus());
            if(statusEnum == JobSchedulerStatusEnum.ERROR || statusEnum == JobSchedulerStatusEnum.PAUSED){
                boolean result = jobInfoService.resume(jobId);
                if(!result){
                    return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, super.informationSwitch("udes.jobinfo.error.task.start"));
                }
            }else{
                return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("udes.jobinfo.error.unconventional.task"));
            }
        }
        return new Response<>().successResponse(super.informationSwitch("exchange.job_info.return.success.info"));
    }

    public void scheduleRun(Long jobId){
        JobInfo jobInfo = jobInfoService.get(jobId);
        if(null != jobInfo){
            JobTask newTask = new JobTask(jobInfo, "scheduler", null);
            List<JobTaskParams> paramsList = new ArrayList<>();
            paramsList.add(new JobTaskParams(DefaultParams.Task.PARAM_DATE, String.valueOf(System.currentTimeMillis())));
            if(jobInfo.getSync() == JobSyncEnum.INCR){
                paramsList.addAll(TaskUtil.generateIncrParams(jobInfo, newTask));
            }
            //Check data source permission
            hasDataSourceAuth(jobInfo.getDataSrcId(), DataAuthScope.DATA_READ, jobInfo.getCreateUser());
            hasDataSourceAuth(jobInfo.getDataDestId(), DataAuthScope.DATA_WRITE, jobInfo.getCreateUser());
            //Before running task, bind and relate executive user and nodes
            bindAndRelateExecNodesAndUser(jobInfo.getExecNodes(), jobInfo.getExecNodeNames(), jobInfo.getExecUser());
            jobTaskService.insertTaskAndAddToQueue(newTask, paramsList);
        }
    }

    private boolean isDuplicate(String jobName, String createUser){
        JobInfoQuery query = new JobInfoQuery();
        query.setCreateUser(createUser);
        query.setJobName(jobName);
        return !jobInfoService.selectAllList(query).isEmpty();
    }

    private void addDataSourceModelParamsToJobParams(Map<String, Object> jobDataParams){
        String modelId = String.valueOf(jobDataParams
                .getOrDefault(JobConstants.FORM_DATASOURCE_MODEL_ID, ""));
        String modelName = String.valueOf(jobDataParams
                .getOrDefault(JobConstants.FORM_DATASOURCE_MODEL_NAME, ""));
        if(StringUtils.isNotBlank(modelId)){
            dataSourceService.addDataSourceModelParamsToMap(
                     jobDataParams,
                    String.valueOf(jobDataParams.getOrDefault(JobConstants.FORM_DATASOURCE_AUTH_ENTITY, "")),
                    String.valueOf(jobDataParams.getOrDefault(JobConstants.FORM_DATASOURCE_AUTH_CREDEN, "")),
                    Integer.valueOf(modelId)
            );
        }else if(StringUtils.isNotBlank(modelName)){
            dataSourceService.addDataSourceModelParamsToMap(
                    jobDataParams,
                    String.valueOf(jobDataParams.getOrDefault(JobConstants.FORM_DATASOURCE_AUTH_ENTITY, "")),
                    String.valueOf(jobDataParams.getOrDefault(JobConstants.FORM_DATASOURCE_AUTH_CREDEN, "")),
                    modelName
            );
        }
    }

    /**
     * Persist disposable auth file
     * @param dataParams
     * @param authFile
     * @return
     * @throws IOException
     */
    private File persistDisAuthFile(Map<String, Object> dataParams, MultipartFile authFile) throws IOException {
        File persistFile = null;
        if(authFile != null && !authFile.isEmpty()){
            logger.info("Persist auth file: " + authFile.getOriginalFilename());
            String authType = String.valueOf(dataParams
                    .getOrDefault(Constants.PARAM_AUTH_TYPE, ""));
            persistFile = dataSourceService.store(authFile, authType, true);
            addAuthFileIntoJobParams(dataParams, authType, persistFile);
        }
        return persistFile;
    }

    private void addAuthFileIntoJobParams(Map<String, Object> params, String authType, File authFile){
        if(AuthType.KERBERS.equals(authType)){
            params.put(Constants.PARAM_KB_FILE_PATH, AppUtil.getIpAndPort() +
                     conf.getStoreUrl(authType) + authFile.getName());
            params.put(Constants.PARAM_KERBEROS_BOOLEAN, true);
        }else if(AuthType.KEYFILE.equals(authType)){
            params.put(Constants.PARAM_KEY_FILE_PATH, AppUtil.getIpAndPort() +
                    conf.getStoreUrl(authType) + authFile.getName());
        }
    }

    /**
     * Bind and relate execution nodes and executive user
     * @param execNodes
     * @param execUser
     */
    private void bindAndRelateExecNodesAndUser(List<Integer> execNodes, List<String> execNodeNames,
                                               String execUser){
        List<ExecNodeUser> execNodeUserList = execNodeInfoService.getExecNodeUserList(execNodes, execUser);
        List<Integer> unbindNodes = new ArrayList<>(execNodes);
        execNodeUserList.forEach(execNodeUser -> {
            int index = unbindNodes.indexOf(execNodeUser.getExecNodeId());
            if( index >= 0){
                unbindNodes.remove(index);
                //Not deep copy
                if(!execNodeNames.isEmpty()) {
                    execNodeNames.remove(index);
                }
            }
            unbindNodes.remove(execNodeUser.getExecNodeId());
        });
        ExecUser execUserEntity = new ExecUser();
        execUserEntity.setExecUser(execUser);
        if(!unbindNodes.isEmpty()) {
            execNodeInfoService.bindAndRelateExecNodesAndUser(unbindNodes, execNodeNames, execUserEntity);
        }
    }

    /**
     * If have data source authority
     * @param dataSourceId
     */
    private void hasDataSourceAuth(Long dataSourceId, DataAuthScope scope, HttpServletRequest request, String operator){
        if(null != dataSourceId && dataSourceId > 0) {
            DataSource dataSource = dataSourceService.get(dataSourceId);
            if (null != dataSource) {
                if (!hasDataAuth(DataSource.class, scope, request, operator, dataSource)) {
                    if (scope.equals(DataAuthScope.DATA_READ)) {
                        throw new EndPointException("exchange.data_source.auth.data_read.not", null, dataSource.getSourceName());
                    } else if (scope.equals(DataAuthScope.DATA_WRITE)) {
                        throw new EndPointException("exchange.data_source.auth.data_write.not", null, dataSource.getSourceName());
                    } else {
                        throw new EndPointException("exchange.data_source.auth.not", null, dataSource.getSourceName());
                    }
                }
            }
        }
    }

    private void hasDataSourceAuth(Long dataSourceId, DataAuthScope scope, HttpServletRequest request){
        hasDataSourceAuth(dataSourceId, scope, request, null);
    }

    private void hasDataSourceAuth(Long dataSourceId, DataAuthScope scope, String operator){
        hasDataSourceAuth(dataSourceId, scope, null, operator);
    }
}
