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

package com.webank.wedatasphere.exchangis.group.service.impl;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.group.dao.GroupDao;
import com.webank.wedatasphere.exchangis.group.dao.UserGroupDao;
import com.webank.wedatasphere.exchangis.group.domain.Group;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.query.UserGroupQuery;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.user.dao.UserInfoDao;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GroupServiceImpl extends AbstractGenericService<Group> implements GroupService {

    @Resource
    private GroupDao groupDao;

    @Resource
    private UserGroupDao userGroupDao;

    @Resource
    private UserInfoDao userInfoDao;

    @Override
    protected IBaseDao<Group> getDao() {
        return groupDao;
    }

    private static final String CH = "(\t|\n|\r|\f|\b)";

    private static Pattern pattern = Pattern.compile(CH);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Group group) {
        this.checkGroupName(group);
        //add group
       if(super.add(group)){
           List<UserGroup> list = group.getUserList();
           for(UserGroup userGroup : list){
               userGroup.setGroupId(group.getId());
           }
           if(list.size() > 0){
                return userGroupDao.insertBatch(list)> 0;
           }
       }
        return false;
    }

    @Override
    public boolean update(Group group) {
        this.checkGroupName(group);
        if(super.update(group)){
            //Find old user list
            UserGroupQuery userGroupQuery = new UserGroupQuery();
            userGroupQuery.setGroupId(group.getId());
            userGroupQuery.setRoleCode(UserGroup.JoinRole.MEMBER.code());
            List<UserGroup> oldUserGroupList = userGroupDao.selectAllList(userGroupQuery);
            GroupService groupService = (GroupService)AopContext.currentProxy();
            if(StringUtils.isNotBlank(group.getCreateUser())) {
                group.getUserList().removeIf(userGroup -> group.getCreateUser().equals(userGroup.getUserName()));
            }
            groupService.updateGroupUserList(group.getId(), oldUserGroupList, group.getUserList());
            return true;
        }else{
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<Object> ids) {
        userGroupDao.deleteGroups(ids);
       return super.delete(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroupUserList(Long groupId, List<UserGroup> oldUserList, List<UserGroup> newUserList) {
        List<String> oldUserNameList = new ArrayList<>();
        List<String> newUserNameList = new ArrayList<>();
        for(UserGroup userGroup : oldUserList){
            oldUserNameList.add(userGroup.getUserName());
        }
        for(UserGroup userGroup : newUserList){
            newUserNameList.add(userGroup.getUserName());
        }
        //Add user
        if(!newUserNameList.isEmpty()) {
            addUsersIfNotExists(newUserNameList);
        }
        List<String> addUserNameList = new ArrayList<>(newUserNameList);
        addUserNameList.removeAll(oldUserNameList);
        //Add user-group relation
        List<UserGroup> addUserGroupRelation = new ArrayList<>();
        addUserNameList.forEach(userName -> addUserGroupRelation.add(new UserGroup(userName, groupId)));
        if(!addUserGroupRelation.isEmpty()){
            userGroupDao.insertBatch(addUserGroupRelation);
        }
        oldUserNameList.removeAll(newUserNameList);
        //Delete user-group relation
        List<UserGroup> deleteUserGroupRelation = new ArrayList<>();
        oldUserNameList.forEach(userName -> deleteUserGroupRelation.add(new UserGroup(userName, groupId)));
        if(!deleteUserGroupRelation.isEmpty()){
            userGroupDao.deleteBatch(deleteUserGroupRelation);
        }
    }

    @Override
    public void checkGroupName(Group group){
        String name = "";
        Matcher matcher = pattern.matcher(group.getGroupName());
        if(matcher.find()){
            group.setGroupName(matcher.replaceAll(name));
        }
        StringEscapeUtils.escapeHtml3(group.getGroupName());
    }

    @Override
    public List<String> queryGroupRefProjectsByUser(String username) {
        return groupDao.selectRefProjectIds(username);
    }

    @Override
    public List<Group> selectByCreatorAndProjects(String createUser, List<Long> projectIds) {
        return groupDao.selectByCreatorAndProjects(createUser, projectIds);
    }

    private void addUsersIfNotExists(List<String> userNames){
        List<String> newUserNameList = new ArrayList<>(userNames);
        List<UserInfo> addUsers = new ArrayList<>();
        List<UserInfo> existUsers = userInfoDao.selectByUsernameList(newUserNameList);
        existUsers.forEach(userInfo -> newUserNameList.remove(userInfo.getUserName()));
        newUserNameList.forEach(userName -> addUsers.add(new UserInfo(userName)));
        if(!addUsers.isEmpty()) {
            userInfoDao.insertBatch(addUsers);
        }
    }
}
