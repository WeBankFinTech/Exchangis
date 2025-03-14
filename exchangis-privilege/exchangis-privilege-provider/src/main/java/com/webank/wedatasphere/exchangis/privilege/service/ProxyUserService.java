package com.webank.wedatasphere.exchangis.privilege.service;

import com.webank.wedatasphere.exchangis.privilege.entity.ProxyUser;
import com.webank.wedatasphere.exchangis.privilege.entity.UserRelationInfo;

import java.util.List;

public interface ProxyUserService {

    boolean havePermission(String realUser, String proxyUser);

    void saveUserRelationInfos(String userName, UserRelationInfo userRelationInfos);

    /**
     * Batch insert
     *
     * @param proxyUsers proxyUsers
     */
    void batchSaveProxyUser(List<ProxyUser> proxyUsers);

    /**
     * Select by username
     *
     * @param userName
     * @return
     */
    List<ProxyUser> selectByUserName(String userName);

}
