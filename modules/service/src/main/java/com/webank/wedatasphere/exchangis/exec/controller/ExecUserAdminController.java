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

import com.webank.wedatasphere.exchangis.auth.annotations.RequireRoles;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecUserQuery;
import com.webank.wedatasphere.exchangis.exec.service.ExecUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author davidhua
 * 2019/11/18
 */
@RestController
@RequireRoles({UserRole.ADMIN})
@RequestMapping("/api/v1/admin/exec/user")
public class ExecUserAdminController extends ExceptionResolverContext {
    private static final String NAME_PATTERN_REGEX = "[a-zA-Z]+[\\d\\w_]+";
    @Resource
    private ExecUserService execUserService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response<Object> listAll(){
        return new Response<>().successResponse(execUserService.listExecUser());
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public Response<Object> add(@Valid @RequestBody ExecUser execUser){
        String userName = execUser.getExecUser();
        if(!userName.matches(NAME_PATTERN_REGEX)){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    this.informationSwitch("exchange.exec_user.name.illegal.not"));
        }
        if(null != execUserService.selectExecUserByName(execUser.getExecUser())){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null ,
                    this.informationSwitch("exchange.exec_user.exists"));
        }
        execUserService.addExecUser(execUser);
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{id:\\d+}", method = RequestMethod.DELETE)
    public Response<Object> delete(@PathVariable("id") Integer id){
        ExecUser execUser = execUserService.selectExecUser(id);
        if(execUser == null){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null ,
                    this.informationSwitch("exchange.exec_user.exists.not"));
        }
        if(execUserService.hasBoundAppUser(execUser.getExecUser())){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    this.informationSwitch("exchange.exec_user.bind.appUser"));
        }
        if(execUserService.hasBoundNode(execUser.getExecUser())){
            return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    this.informationSwitch("exchange.exec_user.bind.node"));
        }
        execUserService.deleteExecUser(id);
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/pager", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<Object> pager(ExecUserQuery query){
        int pageSize = query.getPageSize();
        if(0 == pageSize){
            query.setPageSize(10);
        }
        return new Response<>().successResponse(execUserService.findExecUserPage(query));
    }

}
