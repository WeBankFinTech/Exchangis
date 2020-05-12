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

package com.webank.wedatasphere.exchangis.exec.controller;

import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.SecurityUtil;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.service.ExecUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author davidhua
 * 2019/3/15
 */
@RestController
@RequestMapping("/api/v1/exec/user")
public class ExecUserController extends ExceptionResolverContext {
    @Resource
    private ExecUserService execUserService;

    @Resource
    private SecurityUtil util;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response<Object> list(HttpServletRequest request){
        String username = util.getUserName(request);
        List<String> execUsers = execUserService.getExecUserByAppUser(username);
        Set<String> duplicated = new HashSet<>(execUsers);
        if(duplicated.add(username)){
            ExecUser execUser = new ExecUser();
            execUser.setExecUser(username);
            execUserService.addExecUser(execUser);
        }
        return new Response<>().successResponse(duplicated);
    }
}
