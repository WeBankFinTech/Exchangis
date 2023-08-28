package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisMySQLDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.MYSQL.name;
    }

    @Override
    public String classifier() {
        return Classifier.MYSQL.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is MySQL DataSource";
    }

    @Override
    public String option() {
        return "MySQL数据库";
    }

    @Override
    public String icon() {
        return "icon-mysql";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.MYSQL.name);
    }
}