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

package com.webank.wedatasphere.exchangis.executor.service;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceBase;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteStatus;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.job.domain.JobReport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by devendeng on 2018/9/11.
 * @author devendeng
 */
@FeignClient(name = "exchangis-service")
public interface CallBackService {
    @PostMapping(value = "/api/v1/jobtask/notifyJobComplete")
    Response<String> notifyJobComplete(@RequestParam("taskId") long taskId,
                                       @RequestParam("status") ExecuteStatus status,
                                       @RequestParam("address") String address,@RequestParam("message")String message);

    @PostMapping(value = "/api/v1/jobtask/notifyTaskTimeout")
    Response<String> notifyTaskTimeout(@RequestParam("taskId") long taskId, @RequestParam("message")String message);

    @RequestMapping("/api/v1/exec/node/register")
    Response<String> regiseter(@Valid @RequestBody ExecutorNode node);

    @RequestMapping("/api/v1/exec/node/heartbeat")
    Response<String> heartbeat(@Valid @RequestBody ExecutorNode node);

    @RequestMapping("/api/v1/admin/datasource/view/{id}")
    Response<DataSourceBase> getDataSource(@PathVariable("id") int id);

    @RequestMapping("/api/v1/report/add")
    Response<Object> report(@RequestBody JobReport jobReport);
}
