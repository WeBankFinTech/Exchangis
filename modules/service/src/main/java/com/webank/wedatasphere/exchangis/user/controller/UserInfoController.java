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

package com.webank.wedatasphere.exchangis.user.controller;

import com.webank.wedatasphere.exchangis.auth.annotations.RequireRoles;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.exec.service.ExecUserService;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.query.UserQuery;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequireRoles({UserRole.ADMIN})
@RequestMapping("/api/v1/admin/user")
public class UserInfoController extends AbstractGenericController<UserInfo, UserQuery> {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private ExecUserService execUserService;
    @Resource
    private ExecNodeInfoService execNodeInfoService;

    @Override
    public IBaseService<UserInfo> getBaseService() {
        return userInfoService;
    }

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(UserInfo.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() == UserRole.ADMIN.getValue()){
                //No limit
                return null;
            };
            return new ArrayList<>();
        });
    }
    @Override
    public Response<UserInfo> add(@Valid @RequestBody UserInfo userInfo, HttpServletRequest request) {
        UserInfo userExisted = userInfoService.selectByUsername(userInfo.getUserName());
        if(null != userExisted){
            return new Response<UserInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, "该用户已经存在");
        }
        return super.add(userInfo, request);
    }

    @Override
    public Response<UserInfo> delete(@PathVariable Long id, HttpServletRequest request) {
        String userName = security.getUserName(request);
        UserInfo userInfo = userInfoService.get(id);
        if(null != userInfo && userInfo.getUserName().equals(userName)){
            return new Response<UserInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("exchange.user.info.self.not"));
        }
        if(userInfoService.hasBoundExecUser(userInfo.getUserName())){
            return new Response<UserInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("exchange.user.info.bind.execUser"));
        }
        if(userInfoService.hasBoundExecNode(userInfo.getUserName())){
            return new Response<UserInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("exchange.user.info.bind.node"));
        }
        return super.delete(id, request);
    }

    @Override
    public Response<UserInfo> delBatch(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return new Response<UserInfo>()
                .errorResponse(CodeConstant.SYS_ERROR, null,
                        super.informationSwitch("exchange.api.unsupport"));
    }

    @Override
    public Response<UserInfo> update(@Valid @RequestBody UserInfo updatedUserInfo, HttpServletRequest request) {
        String userName = security.getUserName(request);
        if(null != updatedUserInfo.getId() && updatedUserInfo.getId() > 0) {
            UserInfo storedUser = userInfoService.get(updatedUserInfo.getId());
            if (storedUser.getUserName().equals(userName)) {
                return new Response<UserInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                        super.informationSwitch("exchange.user.info.self.not"));
            }
        }
        UserInfo userInfo = userInfoService.get(updatedUserInfo.getId());
        if(null == userInfo){
            return new Response<UserInfo>().errorResponse(CodeConstant.SYS_ERROR, null,
                    super.informationSwitch("exchange.user.info.exists.not"));
        }
        if(!userInfo.getUserName().equals(updatedUserInfo.getUserName())){
            //Means that the username has been changed, so we should re-encrypt the password, set password to null
            userInfoService.resetPassword(userInfo.getId(), "");
        }
        return super.update(updatedUserInfo, request);
    }

    @RequestMapping(value = "/{appUserId:\\d+}/password/reset", method = RequestMethod.PUT)
    public Response<Object> resetPassword(@PathVariable("appUserId")Integer userId, @RequestBody UserInfo  userInfo){
        boolean result = true;
        if(StringUtils.isNotBlank(userInfo.getPassword())) {
            result = userInfoService.resetPassword(userId, userInfo.getPassword());
        }
        return result ? new Response<>().successResponse("ok") :
                new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                        super.informationSwitch("exchange.user_info.reset.password.error"));
    }

    @RequestMapping(value = "/{appUserId:\\d+}/exec/user/bind/pager", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<Object> bindExecUserPager(@PathVariable("appUserId")Integer appUserId, PageQuery query){
        int pageSize = query.getPageSize();
        if(0 == pageSize){
            query.setPageSize(10);
        }
        return new Response<>().successResponse(userInfoService.findExecUserPage(appUserId, query));
    }

    @RequestMapping(value = "/{appUserId:\\d+}/exec/user/{execUserId:\\d+}/bind", method = RequestMethod.POST)
    public Response<Object> bindExecUser(@PathVariable("appUserId")Integer appUserId,
                                         @PathVariable("execUserId")Integer execUserId){
        UserInfo userInfo = userInfoService.get(appUserId);
        if(null == userInfo){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null ,
                    this.informationSwitch("exchange.user.info.exists.not"));
        }
        ExecUser execUser = execUserService.selectExecUser(execUserId);
        if(null == execUser){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                    this.informationSwitch("exchange.exec_user.exists.not"));
        }
        userInfoService.bindExecUser(userInfo.getUserName(), execUser.getExecUser());
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{appUserId:\\d+}/exec/user/{execUserId:\\d+}/bind", method = RequestMethod.DELETE)
    public Response<Object> unBindExecUser(@PathVariable("appUserId")Integer appUserId,
                                           @PathVariable("execUserId")Integer execUserId){
        UserInfo userInfo = userInfoService.get(appUserId);
        if(null == userInfo){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null ,
                    this.informationSwitch("exchange.user.info.exists.not"));
        }
        ExecUser execUser = execUserService.selectExecUser(execUserId);
        if(null == execUser){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                    this.informationSwitch("exchange.exec_user.exists.not"));
        }
        userInfoService.unbindExecNode(userInfo.getUserName(), execUser.getExecUser());
        return new Response<>().successResponse("success");
    }
    @RequestMapping(value = "/{appUserId:\\d+}/exec/node/bind/pager", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<Object> bindExecNodePager(@PathVariable("appUserId")Integer appUserId, PageQuery query){
        int pageSize = query.getPageSize();
        if(0 == pageSize){
            query.setPageSize(10);
        }
        return new Response<>().successResponse(userInfoService.findExecNodePage(appUserId, query));
    }

    @RequestMapping(value = "/{appUserId:\\d+}/exec/node/{nodeId:\\d+}/bind", method = RequestMethod.POST)
    public Response<Object> bindExecNode(@PathVariable("appUserId")Integer appUserId,
                                         @PathVariable("nodeId")Integer nodeId){
        UserInfo userInfo = userInfoService.get(appUserId);
        if(null == userInfo){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null ,
                    this.informationSwitch("exchange.user.info.exists.not"));
        }
        ExecutorNode node = execNodeInfoService.selectExecNode(nodeId);
        if(null == node){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                    this.informationSwitch("exchange.exec_node.exist.not"));
        }
        userInfoService.bindExecNode(userInfo.getUserName(), node.getId());
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{appUserId:\\d+}/exec/node/{nodeId:\\d+}/bind", method = RequestMethod.DELETE)
    public Response<Object> unBindExecNode(@PathVariable("appUserId")Integer appUserId,
                                           @PathVariable("nodeId")Integer nodeId){
        UserInfo userInfo = userInfoService.get(appUserId);
        if(null == userInfo){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null ,
                    this.informationSwitch("exchange.user.info.exists.not"));
        }
        ExecutorNode node = execNodeInfoService.selectExecNode(nodeId);
        if(null == node){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                    this.informationSwitch("exchange.exec_node.exist.not"));
        }
        userInfoService.unbindExecNode(userInfo.getUserName(), node.getId());
        return new Response<>().successResponse("success");
    }
}
