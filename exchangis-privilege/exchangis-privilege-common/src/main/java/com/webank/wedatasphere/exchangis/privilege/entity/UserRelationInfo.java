package com.webank.wedatasphere.exchangis.privilege.entity;

import java.util.List;

public class UserRelationInfo {

    private String realUser;

    private List<String> proxyUser;

    private List<String> users;

    public String getRealUser() {
        return realUser;
    }

    public void setRealUser(String realUser) {
        this.realUser = realUser;
    }

    public List<String> getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(List<String> proxyUser) {
        this.proxyUser = proxyUser;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public UserRelationInfo() {
    }

}
