package com.webank.wedatasphere.exchangis.datasource.core.vo;

import java.util.List;

public class ExchangisJobInfoContent {

    private ExchangisJobDataSourcesContent dataSources;

    private ExchangisJobParamsContent params;

    private List<ExchangisJobTransformsItem> transforms;

    private List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings;

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

    public List<ExchangisJobTransformsItem> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<ExchangisJobTransformsItem> transforms) {
        this.transforms = transforms;
    }

    public List<ExchangisJobParamsContent.ExchangisJobParamsItem> getSettings() {
        return settings;
    }

    public void setSettings(List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings) {
        this.settings = settings;
    }
}
