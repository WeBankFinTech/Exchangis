package com.webank.wedatasphere.exchangis.job.domain.content;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangisJobInfoContent implements Serializable {

    /**
     * Engine name
     */
    private String engine;

    /**
     * Sub job name
     */
    private String subJobName;

    /**
     * Data source content
     */
    private ExchangisJobDataSourcesContent dataSources;

    /**
     * Extra params
     */
    private ExchangisJobParamsContent params;

    /**
     * Transform define
     */
    private ExchangisJobTransformsContent transforms;

    /**
     * Settings
     */
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
