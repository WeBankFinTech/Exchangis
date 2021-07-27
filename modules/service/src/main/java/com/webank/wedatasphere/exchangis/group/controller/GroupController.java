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

import com.webank.wedatasphere.exchangis.auth.annotations.RequireRoles;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.group.dao.GroupDao;
import com.webank.wedatasphere.exchangis.group.dao.UserGroupDao;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.query.UserGroupQuery;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.group.domain.Group;
import com.webank.wedatasphere.exchangis.group.query.GroupQuery;
import com.webank.wedatasphere.exchangis.group.service.UserGroupService;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Group management
 * Created by devendeng on 2018/8/23.
 *
 * 权限组管理
 */
@RestController
@RequestMapping("/api/v1/group")
public class GroupController extends AbstractGenericController<Group, GroupQuery> {
    private static final String NAME_PATTERN_REGEX = "[a-zA-Z]+[\\d\\w_]+";
    @Resource
    private GroupDao groupDao;
    @Resource
    private GroupService groupService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private UserGroupDao userGroupDao;

    @Resource
    private UserInfoService userInfoService;
    @Override
    public IBaseService<Group> getBaseService() {
        return groupService;
    }

    @PostConstruct
    public void init(){
        //Register user data authority getter, let the admin has the highest authority
        security.registerUserExternalDataAuthGetter(Group.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            };
            return new ArrayList<>();
        });
    }

    @RequestMapping(value = "/view/{id:\\w+}", method = RequestMethod.GET)
    @Override
    public Response<Group> show(@PathVariable Long id, HttpServletRequest request) throws Exception {
        if(!hasDataAuth(getActualType(), DataAuthScope.READ, request, getBaseService().get(id))){
            return new Response<Group>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        UserGroupQuery query = new UserGroupQuery();
        query.setGroupId(id);
        query.setRoleCode(UserGroup.JoinRole.MEMBER.code());
        List<UserGroup> list = userGroupService.selectAllList(query);
        Group t = getBaseService().get(id);
        t.setUserList(list);
        return new Response<Group>().successResponse(t);
    }

    @RequireRoles({UserRole.MANGER})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Override
    public Response<Group> add(@Valid @RequestBody Group group, HttpServletRequest request) {
        security.bindUserInfo(group, request);
        boolean result = groupService.add(group);
        return result ? new Response<Group>().successResponse(null) : new Response<Group>().errorResponse(1, null, "Add failed");

    }

    @RequestMapping(value = "/update/{id:\\w+}", method = RequestMethod.POST)
    @Override
    public Response<Group> update(@Valid @RequestBody Group t,HttpServletRequest request) {
        Group storedGroup = groupService.get(t.getId());
        assert storedGroup != null;
        if(!hasDataAuth(Group.class, DataAuthScope.WRITE, request, storedGroup)) {
            return new Response<Group>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.group.error.insufficient.authority"));
        }
        UserInfo userInfo = userInfoService.selectByUsername(security.getUserName(request));
        if(null == userInfo || userInfo.getUserType() < UserRole.MANGER.getValue()){
            t.setProjectId(storedGroup.getProjectId());
            t.setGroupName(storedGroup.getGroupName());
            t.setGroupDesc(storedGroup.getGroupDesc());
        }
        t.setCreateUser(storedGroup.getCreateUser());
        security.bindUserInfo(t, request);
        boolean result = getBaseService().update(t);
        return result ? new Response<Group>().successResponse(null) : new Response<Group>().errorResponse(1, null, super.informationSwitch("exchange.group.error.update.failed"));
    }

    @Override
    @RequireRoles({UserRole.MANGER})
    public Response<Group> delBatch(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String ids = map.get("ids");
        if(StringUtils.isNotBlank(ids)){
            String[] idsArray = ids.split(",");
            List<Object> idList = Arrays.asList(idsArray);
            return super.delBatch(request, map);
        }
        return new Response<Group>().errorResponse(CodeConstant.PARAMETER_ERROR, null,super.informationSwitch("exchange.group.error.choice.delete"));
    }

    @Override
    @RequireRoles({UserRole.MANGER})
    public Response<Group> delete(Long id, HttpServletRequest request) {
        return super.delete(id, request);
    }


    private boolean checkGroupName(Group group){
        GroupQuery groupQuery = new GroupQuery();
        groupQuery.setGroupName(group.getGroupName());
        List<Group> groups = groupDao.selectAllList(groupQuery);
        return !groups.isEmpty() && (group.getId() == null || groups.size() > 1);
    }
}
