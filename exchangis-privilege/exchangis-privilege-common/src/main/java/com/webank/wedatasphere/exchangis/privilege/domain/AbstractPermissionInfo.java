package com.webank.wedatasphere.exchangis.privilege.domain;

import org.springframework.context.ApplicationContext;

import java.util.Map;

public class AbstractPermissionInfo<T> {

    private String name_cn;

    private String name_en;

    protected Map<Object, String> identify;

    protected T service;

    public AbstractPermissionInfo() {
    }

    public AbstractPermissionInfo(String name_cn, String name_en) {
        this.name_cn = name_cn;
        this.name_en = name_en;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public Map<Object, String> getIdentify() {
        return identify;
    }

    public void setIdentify(ApplicationContext context, String username) {
    }

    public T getService() {
        return service;
    }

    public void setService(T service) {
        this.service = service;
    }
}
