package com.webank.wedatasphere.exchangis.datasource.core.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Model relation
 */
public class DataSourceModelRelation {

    private Long id;

    private Long modelId;

    private Long dsId;

    private String dsName;

    private Long dsVersion;

    private String createUser;

    private Date createTime;

    private String modifyUser;

    private Date modifyTime;

    public DataSourceModelRelation(){
        this.createTime = Calendar.getInstance().getTime();
    }
    public DataSourceModelRelation(String dsName, Long dsVersion, Long modelId){
        super();
        this.dsName = dsName;
        this.dsVersion = dsVersion;
        this.modelId = modelId;
    }
    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getDsId() {
        return dsId;
    }

    public void setDsId(Long dsId) {
        this.dsId = dsId;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public Long getDsVersion() {
        return dsVersion;
    }

    public void setDsVersion(Long dsVersion) {
        this.dsVersion = dsVersion;
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

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
