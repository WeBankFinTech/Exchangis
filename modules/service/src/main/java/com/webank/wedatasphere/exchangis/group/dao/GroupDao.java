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
import com.webank.wedatasphere.exchangis.group.domain.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by devendeng on 2018/8/23.
 */
public interface GroupDao extends IBaseDao<Group> {
    /**
     * Select by ids
     * @param ids
     * @return
     */
     List<Group> selectByIds(List<Object> ids);

    /**
     * Select ref project ids
     * @param userName
     * @return
     */
     List<String> selectRefProjectIds(String userName);

    /**
     * Select by create user and project id
     * @param createUser create user
     * @param projectIds project ids
     * @return
     */
     List<Group> selectByCreatorAndProjects(@Param("createUser") String createUser,
                                            @Param("projectIds") List<Long> projectIds);
}
