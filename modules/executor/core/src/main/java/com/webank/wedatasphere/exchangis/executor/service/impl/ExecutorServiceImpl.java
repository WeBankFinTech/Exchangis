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

package com.webank.wedatasphere.exchangis.executor.service.impl;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.machine.MachineInfo;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.executor.JobContainerManager;
import com.webank.wedatasphere.exchangis.executor.JobExecutor;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteReq;
import com.webank.wedatasphere.exchangis.executor.exception.TaskResAllocException;
import com.webank.wedatasphere.exchangis.executor.resource.ResourceManager;
import com.webank.wedatasphere.exchangis.executor.task.TaskConfigBuilder;
import com.webank.wedatasphere.exchangis.executor.task.log.DefaultLocalTaskLog;
import com.webank.wedatasphere.exchangis.executor.task.process.AbstractJavaInternalTaskProcess;
import com.webank.wedatasphere.exchangis.executor.task.process.datax.DataxTaskProcess;
import com.webank.wedatasphere.exchangis.executor.service.CallBackService;
import com.webank.wedatasphere.exchangis.executor.service.ExecutorService;
import com.webank.wedatasphere.exchangis.executor.task.process.TaskProcessUtils;
import com.webank.wedatasphere.exchangis.executor.util.OperateBarrier;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import com.webank.wedatasphere.exchangis.job.DefaultParams;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by devendeng on 2018/9/5.
 *
 * @author devendeng
 */
@Service
public class ExecutorServiceImpl implements ExecutorService {
    private static Logger LOG = LoggerFactory.getLogger(ExecutorServiceImpl.class);


    @javax.annotation.Resource
    private ExecutorConfiguration configuration;

    @Value("${server.port}")
    private Integer port;


    @javax.annotation.Resource
    private CallBackService callBackService;

    @javax.annotation.Resource
    private ResourceManager resourceManager;

    @javax.annotation.Resource
    private JobContainerManager jobContainerManager;

    /**
     * Operation barriers
     */
    private Map<String, String> operationBarriers = new ConcurrentHashMap<>();
    @PostConstruct
    /**
     * Heartbeat schedule
     */
    public void init() {
        AtomicLong lastTime = new AtomicLong(0);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                List<TaskState> taskStates = jobContainerManager.collectStateList();
                if(!taskStates.isEmpty() || System.currentTimeMillis() - lastTime.get() >= TimeUnit.SECONDS.toMillis(configuration.getIdleHeartBeat())) {
                    lastTime.set(System.currentTimeMillis());
                    String host = MachineInfo.getIpAddress(configuration.getNetworkInterface());
                    if (StringUtils.isBlank(host)) {
                        throw new RuntimeException("Cannot get IP address");
                    }
                    ExecutorNode node = new ExecutorNode();
                    node.setAddress(host + ":" + port);
                    node.setCpuRate((float) MachineInfo.cpuRate(4));
                    node.setMemRate((float) MachineInfo.memoryRate(4));
                    node.setDefaultNode(configuration.isNodeDefault());
                    if(StringUtils.isNotBlank(configuration.getNodeTabs())){
                        node.setTabNames(Arrays.asList(configuration.getNodeTabs().split(",")));
                    }
                    node.setTaskStates(taskStates);
                    Response<String> rsp = callBackService.heartbeat(node);
                    if (rsp.getCode() != 0) {
                        LOG.error("Heartbeat error,{}", rsp.getMessage());
                    }
                }
            } catch (Exception e) {
                LOG.error("Heartbeat error", e);
            }
        }, 5, configuration.getActiveHeartBeat(), TimeUnit.SECONDS);
    }

    @Override
    public boolean run(ExecuteReq req, String execUser) {
        Long jobId = req.getJobId();
        Long taskId = req.getTaskId();
        String jobConfig = req.getJobConfig();
        Map<String, Object> params = req.getTaskParams();
        Long timeout = req.getTimeout();
        boolean result;
        try {
            JobExecutor newJobExecutor = new JobExecutor(jobId, () ->{
                TaskConfiguration taskConfig = TaskConfiguration.from("{}");
                TaskConfigBuilder configBuilder = null;
                LOG.info("Start to build task configuration, jobId: [{}], taskId: [{}]", jobId, taskId);
                try {
                    configBuilder = AppUtil.getBean(TaskConfigBuilder.PREFIX +
                                    String.valueOf(req.getEngine()).toLowerCase(), TaskConfigBuilder.class);
                }catch(BeansException e){
                    //cannot find the handler
                    LOG.info(e.getMessage());
                }
                if(null != configBuilder){
                    taskConfig = configBuilder.build(jobId, taskId, jobConfig, params);
                }
                LOG.info("Build task configuration succeed, jobId:[{}], taskId: [{}]", jobId, taskId);
                String taskExecUser = String.valueOf(params.getOrDefault(DefaultParams.Task.PARAM_EXEC_USER,  ""));
                String execUser0 = execUser;
                if(StringUtils.isNotBlank(taskExecUser)){
                    //Overwrite executive user
                    execUser0 = taskExecUser;
                }
                // buildTaskProcess 会根据引擎来返回对应的 TaskProcess
                return TaskProcessUtils.buildTaskProcess(req.getEngine(), jobId, taskId, execUser0, taskConfig,
                        configuration, req.getEngineParams());
            }, Math.toIntExact(timeout), (process, status, message) -> {
                //complete listener
                LOG.info("Task jobId [{}],taskId [{}],execute complete.status {},message {}",
                        jobId, taskId,status,message);
                Response<String> rsp = callBackService.notifyJobComplete(taskId,status, AppUtil.getIpAndPort(), message);
                if(rsp.getCode() == 0){
                    LOG.info("Notify task [{}] complete, success",taskId);
                }else{
                    LOG.error("TASK_ERROR, notify task [{}] complete error, message {}", taskId, rsp.getMessage());
                }
            });
            if(!jobContainerManager.addContainer(taskId,  new JobContainer(newJobExecutor))){
                return true;
            }
            addHooksToJobExecutor(newJobExecutor);
            //set resourceManager
            newJobExecutor.setResourceManager(resourceManager);
            LOG.trace("Task jobId [{}], taskId [{}] has started to init", jobId, taskId);
            try {
                newJobExecutor.init();
            }catch(Exception e){
                newJobExecutor.clean();
                throw e;
            }
            newJobExecutor.start();
            //allocate actually
            LOG.trace("Waiting for allocating resource for taskId [{}]", taskId);
            if(newJobExecutor.waitForAlloc(configuration.getWaitAllocTimeInSec(), TimeUnit.SECONDS)){
                //CN: 任务调度成功，返回
                LOG.info("Allocate task success, jobId:[{}], taskId:[{}]", new Object[]{jobId, taskId});
                return true;
            }else{
                LOG.trace("Allocate task failed., jobId: [{}], taskId: [{}], reason: cannot allocate resource", jobId, taskId);
                throw new TaskResAllocException("task: ["+ taskId +"] allocate resource failed");
            }
        } catch (Exception e) {
            jobContainerManager.removeContainer(taskId);
            if(e instanceof TaskResAllocException){
                throw (TaskResAllocException)e;
            }
            LOG.error("Scheduler task failed., jobId: [" + jobId + "], taskId: [" + taskId +"]");
            throw new RuntimeException(e);
        }
    }


    @Override
    public Response<String> kill(long jobId, long taskId) {
        return OperateBarrier.operate(String.valueOf(taskId), () ->{
            LOG.info("Kill job {},task {}", jobId, taskId);
            if(killIfExist(jobId, taskId)){
                return new Response<String>().successResponse("success");
            }else{
                LOG.info("Job {},task {},is not exist", jobId, taskId);
                isAlive(jobId, taskId);
                return new Response<String>().errorResponse(501, null, "job " + jobId + ",task " + taskId + ",is not exist");
            }
        });
    }

    @Override
    public JobContainer getJobContainer(Object containerId) {
        return jobContainerManager.getContainer(containerId);
    }

    @Override
    public LogResult log(long jobId, long taskId, int startLine, int windSize) {
        JobContainer jobContainer = jobContainerManager.getContainer(taskId);
        if(null != jobContainer){
            return jobContainer.getRuntime().log(startLine, windSize);
        }
        //history log
        DefaultLocalTaskLog taskLog = new DefaultLocalTaskLog(new File(this.configuration.getJobLogHistory(), jobId + "_" + taskId + ".log"));
        return taskLog.fetchLog(startLine, windSize);
    }

    @Override
    public boolean isAlive(long jobId, long taskId) {
        return OperateBarrier.operate(String.valueOf(taskId), () -> {
            LOG.info("Check if is alive. jobId: {}, taskId: {}", jobId, taskId);
            JobContainer jobContainer = jobContainerManager.getContainer(taskId);
            if(null == jobContainer){
                //TODO choose the process
                AbstractJavaInternalTaskProcess process = new DataxTaskProcess(jobId, taskId,
                        null,TaskConfiguration.from("{}"), configuration, new HashMap<>());
                //clean the work directory
                process.clean();
                return false;
            }
            return true;
        });
    }

    /**
     *
     * @param taskId task id
     * @return if exists
     */
    private boolean killIfExist(long jobId, long taskId){
        JobContainer container = jobContainerManager.getContainer(taskId);
        boolean exists = container != null;
        if (exists) {
            container.getRuntime().toStop();
        }
        return exists;
    }

    private void addHooksToJobExecutor(JobExecutor newJobExecutor){
        //add timeout hook
        newJobExecutor.addHook(JobExecutor.Hook.TASK_TIMEOUT, (taskProcess, message)->{
            long jobId = TaskProcessUtils.getJobId(taskProcess);
            long taskId = TaskProcessUtils.getTaskId(taskProcess);
            LOG.info("Task jobId [{}],taskId [{}] timeout ,message {}", jobId, taskId, message);
            Response<String> rsp = callBackService.notifyTaskTimeout(taskId, message);
            if(rsp.getCode() == 0){
                LOG.info("Notify task [{}] timeout, success", taskId);
            }else{
                LOG.error("Notify task [{}] timeout error, message {}", taskId, rsp.getMessage());
            }
        });
        //add hook to remove container when the task ends
        newJobExecutor.addHook(JobExecutor.Hook.TASK_END, (taskProcess, message) ->
                jobContainerManager.removeContainer(TaskProcessUtils.getTaskId(taskProcess)));
    }
}
