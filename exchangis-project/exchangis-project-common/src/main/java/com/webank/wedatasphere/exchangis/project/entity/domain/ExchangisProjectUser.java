package com.webank.wedatasphere.exchangis.project.entity.domain;

import java.util.Date;

/**
 * @author tikazhang
 * @Date 2022/5/11 10:45
 */
public class ExchangisProjectUser {

    private Long id;

    private Long projectId;

    private String privUser;

    private int priv;

    private Date updateTime;

    public ExchangisProjectUser() {
    }

    public ExchangisProjectUser(Long projectId, String privUser) {
        this.projectId = projectId;
        this.privUser = privUser;
    }

    public ExchangisProjectUser(Long id, Long projectId, String privUser, int priv, Date updateTime) {
        this.id = id;
        this.projectId = projectId;
        this.privUser = privUser;
        this.priv = priv;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPrivUser() {
        return privUser;
    }

    public void setPrivUser(String privUser) {
        this.privUser = privUser;
    }

    public int getPriv() {
        return priv;
    }

    public void setPriv(int priv) {
        this.priv = priv;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date createTime) {
        this.updateTime = updateTime;
    }
}
