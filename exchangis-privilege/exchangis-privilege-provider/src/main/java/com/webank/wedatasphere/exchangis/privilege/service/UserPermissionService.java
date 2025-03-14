package com.webank.wedatasphere.exchangis.privilege.service;

import com.webank.wedatasphere.exchangis.privilege.domain.PermissionDefine;
import com.webank.wedatasphere.exchangis.privilege.domain.PermissionReportData;
import com.webank.wedatasphere.exchangis.privilege.entity.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserPermissionService {

    /**
     * Get permissions
     *
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    PermissionReportData getPermissions() throws InstantiationException, IllegalAccessException;

    /**
     * Get user permission
     *
     * @param userInfo
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    Map<String, Object> getUserPermissions(UserInfo userInfo) throws InstantiationException, IllegalAccessException;

    /**
     * Recycle user
     *
     * @param userInfo
     * @param handover
     */
    void recycleUser(String operator,
                     UserInfo userInfo, String handover);

    /**
     * Recycle user permission
     *
     * @param operator
     * @param username
     * @param handover
     * @param define
     * @param identify
     */
    void recycleUserPermissions(String operator,
                                String username, String handover, PermissionDefine define, List<Object> identify);

}
