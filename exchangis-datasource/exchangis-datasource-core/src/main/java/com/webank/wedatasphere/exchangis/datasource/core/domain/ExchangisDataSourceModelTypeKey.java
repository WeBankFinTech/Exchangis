package com.webank.wedatasphere.exchangis.datasource.core.domain;

import org.apache.linkis.datasourcemanager.common.domain.DataSourceParamKeyDefinition;

public class ExchangisDataSourceModelTypeKey extends DataSourceParamKeyDefinition {

    private Long dsTypeId;

    private String dsType;

    private String nestType;

    private String nestFields;

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

    public String getNestType() {
        return nestType;
    }

    public void setNestType(String nestType) {
        this.nestType = nestType;
    }

    public String getNestFields() {
        return nestFields;
    }

    public void setNestFields(String nestFields) {
        this.nestFields = nestFields;
    }
}
