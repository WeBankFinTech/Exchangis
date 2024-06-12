package com.webank.wedatasphere.exchangis.project.entity.entity;

import java.util.Date;

/**
 * @author jefftlin
 * @date 2024/6/12
 */
public class ExchangisProjectDataSource {

    private Long projectId;

    private String dataSourceName;

    private String createUser;

    private Date createTime;

    public ExchangisProjectDataSource(Long projectId, String dataSourceName, String createUser) {
        this.projectId = projectId;
        this.dataSourceName = dataSourceName;
        this.createUser = createUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
