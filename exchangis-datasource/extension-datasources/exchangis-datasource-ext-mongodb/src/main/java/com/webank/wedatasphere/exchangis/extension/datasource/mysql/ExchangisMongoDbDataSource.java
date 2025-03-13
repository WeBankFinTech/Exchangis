package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

/**
 * Note: MongoDB data source
 */
public class ExchangisMongoDbDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.MONGODB;
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

}