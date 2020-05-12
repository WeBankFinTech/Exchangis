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
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteReq;
import com.webank.wedatasphere.exchangis.executor.domain.ExecuteResp;
import com.webank.wedatasphere.exchangis.route.feign.FeignConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author devendeng
 */
@FeignClient(name = "exchangis-executor")
public interface BalanceRunService {

    /**
     * Use custom loader
     * @param req
     * @return
     */
    @RequestMapping(value = "/api/v1/executor/run")
     Response<ExecuteResp> run(@RequestBody ExecuteReq req);

    @RequestMapping(value = "/api/v1/executor/run")
     Response<ExecuteResp> run(@RequestHeader(FeignConstants.LB_LABEL)String label, ExecuteReq req);
}
