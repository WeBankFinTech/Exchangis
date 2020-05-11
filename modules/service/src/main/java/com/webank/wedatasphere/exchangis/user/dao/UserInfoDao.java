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

package com.webank.wedatasphere.exchangis.user.dao;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Created by devendeng on 2018/8/21.
 */
public interface UserInfoDao extends IBaseDao<UserInfo> {
    /**
     * Insert or update
     * @param userInfo
     */
    void insertOrUpdateOne(UserInfo userInfo);

    /**
     * Select by userName
     * @param userName
     * @return
     */
    UserInfo selectByUsername(String userName);

    /**
     * Select by userName (include password)
     * @param userName
     * @return
     */
    UserInfo selectDetailByUsername(String userName);
    /**
     * Select by userName list
     * @param userNames
     * @return
     */
    List<UserInfo> selectByUsernameList(List<String> userNames);


    /**
     * Insert batch
     * @param users
     */
    void insertBatch(List<UserInfo> users);

    /**
     * Reset password
     * @param id userId
     * @param encryptedPassword password
     * @return
     */
    int resetPassword(@Param("id") Integer id, @Param("password") String encryptedPassword);

}
