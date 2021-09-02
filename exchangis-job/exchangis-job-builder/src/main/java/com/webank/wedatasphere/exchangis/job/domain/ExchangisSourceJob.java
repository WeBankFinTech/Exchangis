package com.webank.wedatasphere.exchangis.job.domain;

import java.util.List;
import java.util.Map;

public class ExchangisSourceJob {

    private String subjobName;

    private Map dataSources;

    private Map params;

    private Map transforms;

    private List settings;

    public String getSubjobName() {
        return subjobName;
    }

    public void setSubjobName(String subjobName) {
        this.subjobName = subjobName;
    }

    public Map getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map datasources) {
        this.dataSources = dataSources;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getTransforms() {
        return transforms;
    }

    public void setTransforms(Map transforms) {
        this.transforms = transforms;
    }

    public List getSettings() {
        return settings;
    }

    public void setSettings(List settings) {
        this.settings = settings;
    }

}
