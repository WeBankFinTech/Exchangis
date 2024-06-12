package com.webank.wedatasphere.exchangis.project.entity.domain;

import java.util.Date;

/**
 * @author jefftlin
 * @date 2024/6/7
 */
public class DSSProjectDataSource {
    private String dataSourceName;
    private String dataSourceDesc;
    private Date createTime;
    private Date modifyTime;
    private String createUser;
    private String dataSourceType;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @Override
    public String toString() {
        return "DSSProjectDataSource{" +
                "dataSourceName='" + dataSourceName + '\'' +
                ", dataSourceDesc='" + dataSourceDesc + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", createUser='" + createUser + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                '}';
    }
}
