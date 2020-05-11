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

package com.webank.wedatasphere.exchangis.executor.controller;

import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.machine.MachineInfo;
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.executor.ExecutorConfiguration;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteReq;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteResp;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.executor.exception.TaskResAllocException;
import com.webank.wedatasphere.exchangis.executor.service.ExecutorService;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author davidhua
 */
@RestController
@RequestMapping("/api/v1/executor")
public class ExecutorController {

    private static Logger LOG = LoggerFactory.getLogger(ExecutorController.class);

    private static final Integer DEFAULT_WINDOW_SIZE = 100;
    @Resource
    private ExecutorService executorService;

    @Resource
    private ExecutorConfiguration configuration;

    @Value("${server.port}")
    private Integer port;

    /**
     * Parameter Exception
     * @param e
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Response errorParameterHandler(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation c : e.getConstraintViolations()) {
            message.append(c.getMessage());
        }
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null, message.toString());
    }

    @ExceptionHandler(value = TaskResAllocException.class)
    public Response errorHandler(TaskResAllocException e){
        return new Response<>().errorResponse(CodeConstant.TASK_ALLOCATE_FAILD, null, e.getMessage());
    }
    /**
     * Global Exception
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Response errorHandler(Exception e) {
        LOG.error("SYSTEM EXCEPTION, message:", e);
        return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, "系统处理异常");
    }
    /**
     * Execute task
     */
    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public Response<ExecuteResp> run(@Valid @RequestBody ExecuteReq req) {
        LOG.trace("Run job {}", req);
        ExecuteResp resp = new ExecuteResp();
        String host = MachineInfo.getIpAddress(configuration.getNetworkInterface());
        resp.setExecutorAddress(host + ":" + port);
        boolean status = false;
        String execUser = req.getExecUser();
        try {
            if(StringUtils.isBlank(execUser)){
                execUser = System.getProperty("user.name", "");
            }
            status  = executorService.run(req, execUser);
        }catch(TaskResAllocException e){
            return new Response<ExecuteResp>().errorResponse(CodeConstant.TASK_ALLOCATE_FAILD, null, e.getMessage());
        }catch (Exception e){
            LOG.error("Execute task "+req.getTaskId()+" error",e);
            resp.setMessage(e.getMessage());
        }
        if(status){
            resp.setStatus(ExecuteStatus.RUNNING);
        }else{
            resp.setStatus(ExecuteStatus.FAILD);
        }
        resp.setExecUser(execUser);
        return new Response<ExecuteResp>().successResponse(resp);
    }

    /**
     * Fetch log
     */
    @RequestMapping(value = "/log/{jobId}/{taskId}/{startLine}/{windSize}", method = RequestMethod.GET)
    public Response<LogResult> log(@Valid @NotBlank(message = "jobId不能为空") @PathVariable long jobId,
                                   @Valid @NotBlank(message = "taskId不能为空") @PathVariable long taskId,
                                   @PathVariable int startLine, @PathVariable("windSize") int windSize) {
        if(windSize == 0){
            windSize = DEFAULT_WINDOW_SIZE;
        }
        LogResult log = executorService.log(jobId,taskId,startLine, windSize);
        return new Response<LogResult>().successResponse(log);
    }

    /**
     * Kill task
     */
    @RequestMapping(value = "/kill/{jobId}/{taskId}", method = RequestMethod.GET)
    public Response<String> kill(@Valid @NotBlank(message = "jobId不能为空") @PathVariable long jobId, @Valid @NotBlank(message = "taskId不能为空") @PathVariable long taskId) {
        try {
            executorService.kill(jobId, taskId);
        }catch(Exception e){
            return new Response<String>().errorResponse(CodeConstant.SYS_ERROR, null, "停止任务失败");
        }
        return new Response<String>().successResponse("success");
    }

    /**
     * Check alive
     * @return
     */
    @RequestMapping(value = "/alive/{jobId}/{taskId}", method = RequestMethod.GET)
    public Response<Boolean> alive(@Valid @NotBlank(message = "jobId不能为空") @PathVariable long jobId, @Valid @NotBlank(message = "taskId不能为空") @PathVariable long taskId){
        return new Response<Boolean>().successResponse(
                executorService.isAlive(jobId, taskId)
        );
    }

    @RequestMapping(value = "/runtime/{jobId}/{taskId}/limit/speed/{byteSpeedLimit:\\w+}", method = RequestMethod.PUT)
    public Response<Object> runtimeLimitSpeed(@PathVariable("jobId")long jobId,
                                              @PathVariable("taskId")long taskId,
                                              @PathVariable("byteSpeedLimit")long byteSpeedLimit){
        JobContainer jobContainer = executorService.getJobContainer(taskId);
        if(null != jobContainer){
            jobContainer.getRuntime().limit(byteSpeedLimit);
        }
        return new Response<>().successResponse("success");
    }
}
