package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisMongoDbDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.MONGODB.name;
    }

    @Override
    public String classifier() {
        return Classifier.MONGODB.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.SEMI_STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is MongoDB DataSource";
    }

    @Override
    public String option() {
        return "mongodb无结构存储";
    }

    @Override
    public String icon() {
        return "icon-mongodb";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.MONGODB.name);
    }
}