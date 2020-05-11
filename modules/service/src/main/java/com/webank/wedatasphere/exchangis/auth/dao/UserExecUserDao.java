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

package com.webank.wedatasphere.exchangis.auth.dao;

import com.webank.wedatasphere.exchangis.auth.domain.UserExecUser;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author davidhua
 * 2019/4/4
 */
public interface UserExecUserDao {
    /**
     * add one
     * @param userExecUser
     */
    void addOne(UserExecUser userExecUser);

    /**
     * delete one
     * @param userExecUser
     */
    void deleteOne(UserExecUser userExecUser);
    /**
     * get by app user
     * @param username
     * @return
     */
    List<String> getExcUserByAppUser(String username);

    /**
     * to find relation of application user and execution user
     * @param appUser
     * @param execUser
     * @return
     */
    Integer exists(@Param("appUser") String appUser, @Param("execUser") String execUser);

    /**
     * check if has the relation
     * @param execUser
     * @return
     */
    Integer existsExecUser(String execUser);

    /**
     * check if has the relation
     * @param appUser
     * @return
     */
    Integer existsAppUser(String appUser);
    /**
     * count
     * @param appUserId
     * @return
     */
    long count(Integer appUserId);

    /**
     * find page
     * @param appUserId
     * @param query
     * @param rowBounds
     * @return
     */
    List<ExecUser> findPageByAppUserId(@Param("appUserId")Integer appUserId, @Param("page")PageQuery query, RowBounds rowBounds);
}
