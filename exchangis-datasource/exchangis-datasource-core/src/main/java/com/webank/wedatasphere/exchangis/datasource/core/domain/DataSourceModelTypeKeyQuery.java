package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;

public class DataSourceModelTypeKeyQuery extends PageQuery {

    private Long id;

    private Long dsTypeId;

    private String dsType;

    private String nestType;

    private String key;

    private String name;

    private String defaultValue;

    private String valueType;

    private String scope;

    private String require;

    private String refId;

    private String refValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefValue() {
        return refValue;
    }

    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }
}
