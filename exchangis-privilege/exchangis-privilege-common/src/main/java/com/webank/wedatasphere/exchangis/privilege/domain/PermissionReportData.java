package com.webank.wedatasphere.exchangis.privilege.domain;

import java.io.Serializable;
import java.util.List;

public class PermissionReportData implements Serializable {

    private static final long serialVersionUID = 1349740214792741L;

    private final Long systemId;

    private final String systemName;

    private final String clusterName;

    private List<SystemPermissionData> systemPermissionDataList;

    private List<RoleData> roleDataList;

    private List<RoleSystemRelateData> roleSystemRelateDataList;

    private List<UserRoleRelateData> userRoleRelateDataList;

    public PermissionReportData(Long systemId, String systemName, String clusterName) {
        this.systemId = systemId;
        this.systemName = systemName;
        this.clusterName = clusterName;
    }

    public void setSystemPermissionDataList(List<SystemPermissionData> systemPermissionDataList) {
        this.systemPermissionDataList = systemPermissionDataList;
    }

    public void setRoleDataList(List<RoleData> roleDataList) {
        this.roleDataList = roleDataList;
    }

    public void setRoleSystemRelateDataList(List<RoleSystemRelateData> roleSystemRelateDataList) {
        this.roleSystemRelateDataList = roleSystemRelateDataList;
    }

    public void setUserRoleRelateDataList(List<UserRoleRelateData> userRoleRelateDataList) {
        this.userRoleRelateDataList = userRoleRelateDataList;
    }

    public Long getSystemId() {
        return systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public List<SystemPermissionData> getSystemPermissionDataList() {
        return systemPermissionDataList;
    }

    public List<RoleData> getRoleDataList() {
        return roleDataList;
    }

    public List<RoleSystemRelateData> getRoleSystemRelateDataList() {
        return roleSystemRelateDataList;
    }

    public List<UserRoleRelateData> getUserRoleRelateDataList() {
        return userRoleRelateDataList;
    }

    public static class SystemPermissionData {

        private String resCode;

        private String resName;

        private String resNameCn;

        private String resContent;

        public SystemPermissionData(String resCode, String resName, String resNameCn, String resContent) {
            this.resCode = resCode;
            this.resName = resName;
            this.resNameCn = resNameCn;
            this.resContent = resContent;
        }

        public String getResCode() {
            return resCode;
        }

        public String getResName() {
            return resName;
        }

        public String getResNameCn() {
            return resNameCn;
        }

        public String getResContent() {
            return resContent;
        }
    }

    public static class RoleData {

        private String roleCode;

        private String roleName;

        private String roleNameCn;

        private String roleContent;

        public RoleData(String roleCode, String roleName, String roleNameCn, String roleContent) {
            this.roleCode = roleCode;
            this.roleName = roleName;
            this.roleNameCn = roleNameCn;
            this.roleContent = roleContent;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getRoleNameCn() {
            return roleNameCn;
        }

        public String getRoleContent() {
            return roleContent;
        }
    }

    public static class RoleSystemRelateData {

        private String resCode;

        private String roleCode;

        public RoleSystemRelateData(String resCode, String roleCode) {
            this.resCode = resCode;
            this.roleCode = roleCode;
        }

        public String getResCode() {
            return resCode;
        }

        public String getRoleCode() {
            return roleCode;
        }
    }

    public static class UserRoleRelateData {

        private String roleCode;

        private Long userId;

        private String userName;

        public UserRoleRelateData(String roleCode, Long userId, String userName) {
            this.roleCode = roleCode;
            this.userId = userId;
            this.userName = userName;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }

    public static class Builder {

        private PermissionReportData permissionReportData;

        public Builder(Long systemId, String systemName, String clusterName) {
            permissionReportData = new PermissionReportData(
                    systemId, systemName, clusterName);
        }

        public Builder withSystemPermissionData(List<SystemPermissionData> systemPermissionDataList) {
            permissionReportData.setSystemPermissionDataList(systemPermissionDataList);
            return this;
        }

        public Builder withRoleData(List<RoleData> roleDataList) {
            permissionReportData.setRoleDataList(roleDataList);
            return this;
        }

        public Builder withRoleSystemRelateData(List<RoleSystemRelateData> userRoleRelateDataList) {
            permissionReportData.setRoleSystemRelateDataList(userRoleRelateDataList);
            return this;
        }

        public Builder withUserRoleRelateData(List<UserRoleRelateData> userRoleRelateDataList) {
            permissionReportData.setUserRoleRelateDataList(userRoleRelateDataList);
            return this;
        }

        public PermissionReportData build() {
            return this.permissionReportData;
        }
    }
}
