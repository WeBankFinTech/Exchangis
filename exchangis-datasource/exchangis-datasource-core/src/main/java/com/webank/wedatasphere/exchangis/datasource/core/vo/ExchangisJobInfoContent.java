package com.webank.wedatasphere.exchangis.datasource.core.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangisJobInfoContent {

    private String engine;

    private String subJobName;

    private ExchangisJobDataSourcesContent dataSources;

    private ExchangisJobParamsContent params;

//    private List<ExchangisJobTransformsItem> transforms;
    private ExchangisJobTransformsContent transforms;

    private List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public ExchangisJobDataSourcesContent getDataSources() {
        return dataSources;
    }

    public void setDataSources(ExchangisJobDataSourcesContent dataSources) {
        this.dataSources = dataSources;
    }

    public ExchangisJobParamsContent getParams() {
        return params;
    }

    public void setParams(ExchangisJobParamsContent params) {
        this.params = params;
    }

    public ExchangisJobTransformsContent getTransforms() {
        return transforms;
    }

    public void setTransforms(ExchangisJobTransformsContent transforms) {
        this.transforms = transforms;
    }

    public List<ExchangisJobParamsContent.ExchangisJobParamsItem> getSettings() {
        return settings;
    }

    public void setSettings(List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings) {
        this.settings = settings;
    }

    public String getSubJobName() {
        return subJobName;
    }

    public void setSubJobName(String subJobName) {
        this.subJobName = subJobName;
    }
}
