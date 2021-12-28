package com.webank.wedatasphere.exchangis.project.server.entity;

import java.util.Date;

public class ExchangisProject {

    public static enum Domain {
        WORKSPACE, STANDALONE
    }

    private Long id;
    private Long dssProjectId;
    private String name;
    private String dssName;
    private String description;
    private String createBy;
    private Date createTime;
    private String lastUpdateBy;
    private Date lastUpdateTime;
    private String tags;
    private String editUsers;
    private String viewUsers;
    private String execUsers;
    private String workspaceName;
    private String domain;

//    public ExchangisProject(String name, String description, String workspaceName){
//        this.name = name;
//        this.description = description;
//        this.workspaceName = workspaceName;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDssName() { return dssName; }

    public void setDssName(String dssName) { this.dssName = dssName; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Long getDssProjectId() {
        return dssProjectId;
    }

    public void setDssProjectId(Long dssProjectId) {
        this.dssProjectId = dssProjectId;
    }

    public String getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(String editUsers) {
        this.editUsers = editUsers;
    }

    public String getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(String viewUsers) {
        this.viewUsers = viewUsers;
    }

    public String getExecUsers() {
        return execUsers;
    }

    public void setExecUsers(String execUsers) {
        this.execUsers = execUsers;
    }

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }
}
