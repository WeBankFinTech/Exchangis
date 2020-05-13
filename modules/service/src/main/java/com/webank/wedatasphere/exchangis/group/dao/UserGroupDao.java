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

package com.webank.wedatasphere.exchangis.group.dao;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.query.UserGroupQuery;

import java.util.List;
import java.util.Set;

public interface UserGroupDao extends IBaseDao<UserGroup> {
    /**
     *
     * @param userName
     * @return
     */
    Set<String> queryGroupUser(String userName);

    /**
     *
     * @param userGroup
     * @return
     */
    int delUser(UserGroup userGroup);

    /**
     *
     * @param list
     * @return
     */
    int insertBatch(List<UserGroup> list);

    /**
     *
     * @param list
     */
    void deleteBatch(List<UserGroup> list);

    /**
     *
     * @param groupId
     * @return
     */
    int deleteByGroupId(Integer groupId);

    /**
     *
     * @param ids
     * @return
     */
    boolean deleteGroups(List<Object> ids);

    /**
     *
     * @param query
     * @return
     */
    List<UserGroup> getAvailableUser(UserGroupQuery query);

}

