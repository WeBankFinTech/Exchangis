package com.webank.wedatasphere.exchangis.datasource.vo;

import java.util.Map;

public class DataSourceCreateVO {
    private String name;

    private Integer typeId;

    private Map<String, Object> connectParams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }
}
