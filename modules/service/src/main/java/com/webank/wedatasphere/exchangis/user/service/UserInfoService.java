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

package com.webank.wedatasphere.exchangis.user.service;

import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;

/**
 * @author davidhua
 * 2019/4/9
 */
public interface UserInfoService extends IBaseService<UserInfo> {
    /**
     * Sync user info into database
     * @param userInfo
     */
    void sync(UserInfo userInfo);

    /**
     * Select by username
     * @param userName
     * @return
     */
    UserInfo selectByUsername(String userName);

    UserInfo selectDetailByUsername(String userName);
    /**
     * Find executive user by app user
     * @param appUserId
     * @param query
     * @return
     */
    PageList<ExecUser> findExecUserPage(Integer appUserId, PageQuery query);

    /**
     * Bind executive user
     * @param appUser
     * @param execUser
     */
    void bindExecUser(String appUser, String execUser);

    /**
     * Unbind executive user
     * @param appUser
     * @param execUser
     */
    void unbindExecNode(String appUser, String execUser);
    /**
     * Find execution node by app user
     * @param appUserId
     * @param query
     * @return
     */
    PageList<ExecutorNode> findExecNodePage(Integer appUserId, PageQuery query);

    /**
     * Bind execution node
     * @param appUser
     * @param nodeId
     */
    void bindExecNode(String appUser, Integer nodeId);

    /**
     * Unbind execution node
     * @param appUser
     * @param nodeId
     */
    void unbindExecNode(String appUser, Integer nodeId);

    /**
     * Check if it has bound executive user
     * @param appUser
     * @return
     */
    boolean hasBoundExecUser(String appUser);

    /**
     * Check if it has bound execution node
     * @param appUser
     * @return
     */
    boolean hasBoundExecNode(String appUser);

    /**
     * Reset password
     * @param id
     * @param password
     */
    boolean resetPassword(Integer id, String password);

    UserInfo createUser(String username);
}
