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
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeTabRelation;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUser;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUserBind;
import com.webank.wedatasphere.exchangis.exec.query.ExecNodeUserQuery;
import com.webank.wedatasphere.exchangis.exec.service.ExecNodeInfoService;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.exec.query.ExecNodeQuery;
import com.webank.wedatasphere.exchangis.exec.service.impl.ExecNodeServiceImpl;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Executor node management
 * @author davidhua
 * 2019/4/9
 */
@RestController
@RequireRoles({UserRole.ADMIN})
@RequestMapping("/api/v1/admin/exec/node")
public class ExecNodeAdminController extends AbstractGenericController<ExecutorNode, ExecNodeQuery> {
    @Resource
    private ExecNodeServiceImpl executorNodeService;

    @Resource
    private ExecNodeInfoService execNodeInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public IBaseService<ExecutorNode> getBaseService() {
        return executorNodeService;
    }

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(ExecutorNode.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.ADMIN.getValue()){
                //No limit
                return null;
            }
            return new ArrayList<>();
        });
    }
    @RequestMapping(value = "/{nodeId:\\d+}/user/bind", method = RequestMethod.POST)
    public Response<Object> bind(@PathVariable("nodeId")Integer nodeId,
                                 @Valid @RequestBody ExecNodeUserBind bind){
        bind.setNodeId(nodeId);
        int success = execNodeInfoService.bindExecNodeAndUsers(bind);
        if(success < bind.getExecUserList().size() && bind.getOpType() == ExecNodeUserBind.BindOpType.BIND_RELATE){
            return new Response<>().errorResponse(CodeConstant.WARNING_MSG, null,
                    this.informationSwitch("exchange.exec_node.user.bind-condition.fail"),
                    success, bind.getExecUserList().size() - success);
        }
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{nodeId:\\d+}/user/bind/pager", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<Object> bindPager(@PathVariable("nodeId")Integer nodeId,
                                      ExecNodeUserQuery query){
        query.setNodeId(nodeId);
        int pageSize = query.getPageSize();
        if(0 == pageSize){
            query.setPageSize(10);
        }
        return new Response<>().successResponse(execNodeInfoService.findExecNodeUserPage(query));
    }
    @RequestMapping(value = "/{nodeId:\\d+}/user/{username}/bind", method = RequestMethod.DELETE)
    public Response<Object> unBind(@PathVariable("nodeId")Integer nodeId, @PathVariable("username")String username){
        ExecNodeUser execNodeUser = execNodeInfoService.getExecNodeUser(nodeId, username);
        if(null != execNodeUser){
            execNodeInfoService.unBindExecNodeAndUser(execNodeUser);
        }
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{nodeId:\\d+}/user/{username}/relate", method = RequestMethod.PUT)
    public Response<Object> relate(@PathVariable("nodeId")Integer nodeId, @PathVariable("username")String username){
        ExecNodeUser execNodeUser = execNodeInfoService.getExecNodeUser(nodeId, username);
        if(null != execNodeUser && execNodeUser.getRelationState() != 1) {
            execNodeInfoService.relateExecNodeAndUser(null, execNodeUser);
        }
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{nodeId:\\d+}/user/{username}/relate", method = RequestMethod.DELETE)
    public Response<Object> notRelate(@PathVariable("nodeId")Integer nodeId, @PathVariable("username")String username){
        ExecNodeUser execNodeUser = execNodeInfoService.getExecNodeUser(nodeId,  username);
        if(null != execNodeUser && execNodeUser.getRelationState() == 1){
            execNodeInfoService.notRelateExecNodeAndUser(execNodeUser);
        }
        return new Response<>().successResponse("success");
    }

    /**
     * Attach tab
     * @return
     */
    @RequestMapping(value = "/{nodeId:\\d+}/tab", method = RequestMethod.POST)
    public Response<Object> attachTab(@PathVariable("nodeId")Integer nodeId,
                                      @Valid @RequestBody ExecNodeTabRelation relation){
        relation.setNodeId(nodeId);
        execNodeInfoService.attachTab(relation);
        return new Response<>().successResponse("success");
    }

    @RequestMapping(value = "/{nodeId:\\d+}/default", method = RequestMethod.POST)
    public Response<Object> makeDefault(@PathVariable("nodeId")Integer nodeId){
        return execNodeInfoService.changeDefault(nodeId, true) ?
                new Response<>().successResponse("success") : new Response<>().errorResponse(-1, null, "fail");
    }

    @RequestMapping(value = "/{nodeId:\\d+}/default", method = RequestMethod.DELETE)
    public Response<Object> cancelDefault(@PathVariable("nodeId")Integer nodeId){
        return execNodeInfoService.changeDefault(nodeId, false) ?
                new Response<>().successResponse("success") : new Response<>().errorResponse(-1, null, "fail");
    }
    @Override
    public Response<ExecutorNode> delete(@PathVariable Long id, HttpServletRequest request) {
        //Delete node information and relations
        execNodeInfoService.deleteNode(id);
        return new Response<ExecutorNode>().successResponse(null);
    }
}
