package com.webank.wedatasphere.exchangis.job.server.entity;

import java.util.Date;

/**
 * @author tikazhang
 */
public class ExchangisLaunchableTaskEntity {
    private Long id;

    private String name;

    private Date createTime;

    private Date lastUpdateTime;

    private String engineType;

    private String executeUser;

    private String linkisJobName;

    private String linkisJobContent;

    private String linkisParams;

    private String linkisSource;

    private String labels;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getLinkisJobName() {
        return linkisJobName;
    }

    public void setLinkisJobName(String linkisJobName) {
        this.linkisJobName = linkisJobName;
    }

    public String getLinkisJobContent() {
        return linkisJobContent;
    }

    public void setLinkisJobContent(String linkisJobContent) {
        this.linkisJobContent = linkisJobContent;
    }

    public String getLinkisParams() {
        return linkisParams;
    }

    public void setLinkisParams(String linkisParams) {
        this.linkisParams = linkisParams;
    }

    public String getLinkisSource() {
        return linkisSource;
    }

    public void setLinkisSource(String linkisSource) {
        this.linkisSource = linkisSource;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }
}
