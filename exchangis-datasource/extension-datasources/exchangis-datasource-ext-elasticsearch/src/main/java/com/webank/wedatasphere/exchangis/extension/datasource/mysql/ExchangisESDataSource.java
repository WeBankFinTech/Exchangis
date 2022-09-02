package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisESDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.ELASTICSEARCH.name;
    }

    @Override
    public String classifier() {
        return Classifier.ELASTICSEARCH.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.NON_STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is ES DataSource";
    }

    @Override
    public String option() {
        return "ES无结构化存储";
    }

    @Override
    public String icon() {
        return "icon-es";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.ELASTICSEARCH.name);
    }
}