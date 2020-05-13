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
import com.webank.wedatasphere.exchangis.group.dao.UserGroupDao;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.query.UserGroupQuery;
import com.webank.wedatasphere.exchangis.group.service.UserGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserGroupServiceImpl  extends AbstractGenericService<UserGroup> implements UserGroupService{
    @Resource
    private UserGroupDao userGroupDao;
    @Override
    protected IBaseDao<UserGroup> getDao() {
        return userGroupDao;
    }

    @Override
    public Set<String> queryGroupUser(String userName) {
        Set<String> set = userGroupDao.queryGroupUser(userName);
        if(set == null) {
            set = new HashSet<>();
        }
        set.add(userName);
        return set;
    }

    @Override
    public boolean delUser(UserGroup userGroup) {
        return userGroupDao.delUser(userGroup) > 0;
    }

    @Override
    public List<UserGroup> getAvailableUser(UserGroupQuery query) {
        return userGroupDao.getAvailableUser(query);
    }
}
