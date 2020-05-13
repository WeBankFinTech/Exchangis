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

package com.webank.wedatasphere.exchangis.group.controller;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractDataAuthController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.group.domain.Group;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.query.UserGroupQuery;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.group.service.UserGroupService;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User group management
 */
@RestController
@RequestMapping("/api/v1/usergroup")
public class UserGroupController extends AbstractDataAuthController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserGroupService userGroupService;

    @Resource
    private GroupService groupService;

    /**
     * Delete user under the group
     * @param userGroup user group entity
     * @param request request
     * @return
     */
    @RequestMapping(value = "/delUser", method = RequestMethod.POST)
    public Response<UserGroup> delUser(@RequestBody UserGroup userGroup, HttpServletRequest request) {
        if(!hasDataAuth(Group.class, DataAuthScope.WRITE, request, groupService.get(userGroup.getGroupId()))) {
            return new Response<UserGroup>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.user_group.not.insufficient.authority"));
        }
        boolean result = userGroupService.delUser(userGroup);
        return result ? new Response<UserGroup>().successResponse(null) : new Response<UserGroup>().errorResponse(1, null, super.informationSwitch("exchange.user_group.delete.failed.info"));
    }

    /**
     * Get addable user list
     * @param request request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getAvailableUser", method = RequestMethod.POST)
    public Response<List<UserGroup>> getAvailableUser(HttpServletRequest request) throws Exception {
        //No limit
        Long groupId = null;
        String groupIdStr = request.getParameter("groupId");
        if(StringUtils.isNotEmpty(groupIdStr)){
            groupId = Long.parseLong(groupIdStr);
        }
        String userName = request.getParameter("userName");
        //query user in group
        UserGroupQuery query = new UserGroupQuery();
        query.setGroupId(groupId);
        query.setUserName(userName);
        List<UserGroup> list= userGroupService.getAvailableUser(query);
        return new Response<List<UserGroup>>().successResponse(list);
    }

}
