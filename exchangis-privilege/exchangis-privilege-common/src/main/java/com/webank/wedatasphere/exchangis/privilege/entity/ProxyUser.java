package com.webank.wedatasphere.exchangis.privilege.entity;

import java.util.Date;

public class ProxyUser {

    private Long id;
    private String userName;
    private String proxyUserName;
    private String createBy;
    private Date createTime;
    private Date updateTime;
    private String remark;

    public ProxyUser() {
    }

    public ProxyUser(String userName, String proxyUserName) {
        this.userName = userName;
        this.proxyUserName = proxyUserName;
    }

    public ProxyUser(Long id, String userName, String proxyUserName, String createBy, Date createTime, Date updateTime, String remark) {
        this.id = id;
        this.userName = userName;
        this.proxyUserName = proxyUserName;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ProxyUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", proxyUserName='" + proxyUserName + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
