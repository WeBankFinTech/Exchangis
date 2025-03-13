package com.webank.wedatasphere.exchangis.privilege.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserInfo {

    private Long id;
    private String userName;
    private String password;
    private Integer userType = 0;
    private String orgCode;
    private String deptCode;
    private Date createTime;
    private Date updateTime;

    /**
     * Exec user names
     */
    private List<String> execUsers = new ArrayList<>();

    public UserInfo(String userName){
        this.userName = userName;
        this.userType = 0;
        this.updateTime = Calendar.getInstance().getTime();
    }

    public UserInfo(){
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public List<String> getExecUsers() {
        return execUsers;
    }

    public void setExecUsers(List<String> execUsers) {
        this.execUsers = execUsers;
    }

    public String getRole(){
        if(userType != null) {
            if (userType == 0) {
                return "user";
            } else if (userType == 1) {
                return "admin";
            } else if (userType == 2) {
                return "super";
            }
        }
        return "unLogin";
    }
}
