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

package com.webank.wedatasphere.exchangis.group.service;

import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.group.domain.Group;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;

import java.util.List;

/**
 * Created by devendeng on 2018/8/23.
 */

public interface GroupService extends IBaseService<Group> {
    /**
     * Update user list under the group
     * @param oldUserList
     * @param newUserList
     */
    void updateGroupUserList(Long groupId, List<UserGroup> oldUserList, List<UserGroup> newUserList);

    void checkGroupName(Group group);

    /**
     * Query projects referred group that the user participate in
     * @param username
     * @return
     */
    List<String> queryGroupRefProjectsByUser(String username);

    /**
     * Select by create user and project id
     * @param createUser create user
     * @param projectIds project ids
     * @return
     */
    List<Group> selectByCreatorAndProjects(String createUser, List<Long> projectIds);
}
