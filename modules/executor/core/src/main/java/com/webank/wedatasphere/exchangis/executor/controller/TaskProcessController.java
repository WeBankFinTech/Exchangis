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
import com.webank.wedatasphere.exchangis.executor.JobContainer;
import com.webank.wedatasphere.exchangis.job.domain.JobReport;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.executor.service.CallBackService;
import com.webank.wedatasphere.exchangis.executor.service.ExecutorService;
import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author davidhua
 * 2019/4/15
 */
@RestController
@RequestMapping("/api/v1/task/process")
public class TaskProcessController{
    private static final Logger LOG = LoggerFactory.getLogger(TaskProcessController.class);
    @Resource
    private ExecutorService executorService;
    @Resource
    private CallBackService callBackService;

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public Response<Object> report(@RequestBody JobReport jobReport){
        LOG.info("Report obj : {}", Json.toJson(jobReport,null));
        JobContainer jobContainer = executorService.getJobContainer(jobReport.getId());
        if(null != jobContainer){
            jobReport.setJobId(jobContainer.getJobId());
            return callBackService.report(jobReport);
        }
        return new Response<>().successResponse(true);
    }

    @RequestMapping(value = "/report/state", method = RequestMethod.POST)
    public Response<Object> state(@RequestBody TaskState taskState){
        Map<String, Object> runtimeParams = new HashMap<>(1);
        JobContainer jobContainer = executorService.getJobContainer(taskState.getTaskId());
        if(null != jobContainer){
            jobContainer.updateTaskState(taskState);
            runtimeParams.put("maxByteSpeed", jobContainer.getRuntime().getMaxByteSpeed());
        }
        //return runtime parameters
        return new Response<>().successResponse(runtimeParams);
    }
}
