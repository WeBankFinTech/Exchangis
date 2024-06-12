package com.webank.wedatasphere.exchangis.project.entity.entity;

import java.util.Date;

/**
 * Project data source relation
 */
public class ExchangisProjectDsRelation {
    /**
     * Relation id
     */
    private Long id;

    /**
     * Project id
     */
    private Long projectId;
    /**
     * Data source name
     */
    private String dsName;

    /**
     * Data source creator
     */
    private String dsCreator;

    /**
     * Last update time
     */
    private Date lastUpdateTime;

    public ExchangisProjectDsRelation(){

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

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDsCreator() {
        return dsCreator;
    }

    public void setDsCreator(String dsCreator) {
        this.dsCreator = dsCreator;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
