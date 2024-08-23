package com.webank.wedatasphere.exchangis.datasource.core.domain;

import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;

import java.util.Date;

public class DataSourceModelTypeKey extends DataSourceParamKeyDefinition {

    private Long dsTypeId;

    private String dsType;

    private ValueType nestType;

    private String nestFields;

    private Date createTime;

    private Date modifyTime;

    public Long getDsTypeId() {
        return dsTypeId;
    }

    public void setDsTypeId(Long dsTypeId) {
        this.dsTypeId = dsTypeId;
    }

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public ValueType getNestType() {
        return nestType;
    }

    public void setNestType(ValueType nestType) {
        this.nestType = nestType;
    }

    public String getNestFields() {
        return nestFields;
    }

    public void setNestFields(String nestFields) {
        this.nestFields = nestFields;
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
}
