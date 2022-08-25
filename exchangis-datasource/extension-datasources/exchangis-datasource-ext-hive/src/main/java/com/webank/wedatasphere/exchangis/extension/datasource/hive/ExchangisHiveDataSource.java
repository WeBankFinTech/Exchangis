package com.webank.wedatasphere.exchangis.extension.datasource.hive;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisHiveDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.HIVE.name;
    }

    @Override
    public String classifier() {
        return Classifier.HIVE.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.SEMI_STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is Hive DataSource";
    }

    @Override
    public String option() {
        return "hive";
    }

    @Override
    public String icon() {
        return "icon-hive";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.HIVE.name);
    }
}
