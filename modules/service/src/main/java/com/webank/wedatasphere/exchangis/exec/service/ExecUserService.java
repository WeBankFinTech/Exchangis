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

package com.webank.wedatasphere.exchangis.exec.service;

import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecUserQuery;

import java.util.List;

/**
 * @author davidhua
 * 2019/11/18
 */
public interface ExecUserService {
    /**
     * Check if the user has the permission of this executive user
     * @param appUser
     * @param execUser
     * @return
     */
    boolean havePermission(String appUser, String execUser);
    /**
     * Get exec user
     * @param appUser
     * @return
     */
    List<String> getExecUserByAppUser(String appUser);

    /**
     * List all executive users
     * @return
     */
    List<ExecUser> listExecUser();

    /**
     * Add user
     * @param execUser
     */
    void addExecUser(ExecUser execUser);

    /**
     * Select by name
     * @param execUser
     * @return
     */
    ExecUser selectExecUserByName(String execUser);

    /**
     * Select by id
     * @param id
     * @return
     */
    ExecUser selectExecUser(Integer id);

    /**
     * Delete
     * @param id
     */
    void deleteExecUser(Integer id);

    /**
     * Find pager
     * @param query
     * @return
     */
    PageList<ExecUser> findExecUserPage(ExecUserQuery query);

    /**
     *  Check if has bound node
     * @param execUser
     * @return
     */
    boolean hasBoundNode(String execUser);

    /**
     * Check if has bound app user
     * @param execUser
     * @return
     */
    boolean hasBoundAppUser(String execUser);
}
