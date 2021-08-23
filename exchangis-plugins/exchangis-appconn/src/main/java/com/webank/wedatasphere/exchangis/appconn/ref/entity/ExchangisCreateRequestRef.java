package com.webank.wedatasphere.exchangis.appconn.ref.entity;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.crud.CreateRequestRef;

import java.util.Map;

public class ExchangisCreateRequestRef implements CreateRequestRef {

    private String name;
    private String type;
    private Map<String, Object> parameters = Maps.newHashMap();

    @Override
    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
