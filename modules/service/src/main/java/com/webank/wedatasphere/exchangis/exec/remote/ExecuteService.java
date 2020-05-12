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

package com.webank.wedatasphere.exchangis.exec.remote;

import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.executor.domain.ExecSysUser;
import com.webank.wedatasphere.exchangis.job.domain.LogResult;
import com.webank.wedatasphere.exchangis.route.feign.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by devendeng on 2018/9/13.
 * @author devendeng
 */
@FeignClient(name = "exchangis-executor")
public interface ExecuteService {
    /**
     * To fetch task log from remote server
     * @param jobId
     * @param taskId
     * @param startLine
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/log/{jobId}/{taskId}/{startLine}/{windSize}", method = RequestMethod.GET)
    Response<LogResult> log(@RequestHeader(FeignConstants.HOST_PORT)String hostPort,
                            @PathVariable("jobId") long jobId, @PathVariable("taskId") long taskId, @PathVariable("startLine") int startLine,
                            @PathVariable("windSize")int windSize);

    /**
     * Kill task
     * @param taskId task id
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/kill/{jobId}/{taskId}", method = RequestMethod.GET)
    Response<String> kill(@RequestHeader(FeignConstants.HOST_PORT)String hostPort,
                          @PathVariable("jobId") long jobId,@PathVariable("taskId") long taskId);

    /**
     * Check alive in machine specific
     * @param hostPort
     * @param jobId
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/alive/{jobId}/{taskId}", method = RequestMethod.GET)
    Response<Boolean> alive(@RequestHeader(FeignConstants.HOST_PORT)String hostPort,
                            @PathVariable("jobId") long jobId, @PathVariable("taskId")long taskId);

    /**
     * runtimeLimitSpeed
     * @param hostPort
     * @param jobId
     * @param tasId
     * @param cc
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/runtime/{jobId}/{taskId}/limit/speed/{byteSpeedLimit}", method = RequestMethod.PUT)
    Response<String> runtimeLimitSpeed(@RequestHeader(FeignConstants.HOST_PORT) String hostPort,
                                       @PathVariable("jobId")long jobId,
                                       @PathVariable("taskId")long tasId,
                                       @PathVariable("byteSpeedLimit")long cc);

    /**
     * Create system user
     * @param hostPort
     * @param username
     * @param uid
     * @param gid
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/env/user/{username}/{uid}/{gid}", method = RequestMethod.POST)
    Response<ExecSysUser> createSysUser(@RequestHeader(FeignConstants.HOST_PORT) String hostPort, @PathVariable("username") String username,
                                        @PathVariable("uid") Integer uid, @PathVariable("gid") Integer gid);

    /**
     * Delete user
     * @param hostPort
     * @param username
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/env/user/{username}", method = RequestMethod.DELETE)
    Response<String> deleteSysUser(@RequestHeader(FeignConstants.HOST_PORT) String hostPort,@PathVariable("username") String username);
}
