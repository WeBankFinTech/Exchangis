package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;


/**
 * Note: MYSQL data source
 */
public class ExchangisMySQLDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.MYSQL;
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


}