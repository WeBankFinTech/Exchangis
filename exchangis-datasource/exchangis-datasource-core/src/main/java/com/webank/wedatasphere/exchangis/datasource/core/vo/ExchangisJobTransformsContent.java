package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExchangisJobTransformsContent {
    private boolean addEnable;
    private String type;
    private String sql;

    @JsonProperty("code_id")
    private String codeId;

    private List<ExchangisJobTransformsItem> mapping;

    public boolean isAddEnable() {
        return addEnable;
    }

    public void setAddEnable(boolean addEnable) {
        this.addEnable = addEnable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ExchangisJobTransformsItem> getMapping() {
        return mapping;
    }

    public void setMapping(List<ExchangisJobTransformsItem> mapping) {
        this.mapping = mapping;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
}
