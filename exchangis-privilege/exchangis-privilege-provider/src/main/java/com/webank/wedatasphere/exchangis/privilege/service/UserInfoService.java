package com.webank.wedatasphere.exchangis.privilege.service;

import com.webank.wedatasphere.exchangis.privilege.entity.UserInfo;

import java.util.List;

public interface UserInfoService {

    /**
     * Select all user
     *
     * @return
     */
    List<UserInfo> selectAllUser();

    /**
     * Select by username
     *
     * @param userName
     * @return
     */
    UserInfo selectByUsername(String userName);

    UserInfo selectDetailByUsername(String userName);

    void saveUser(UserInfo userInfo);

}
