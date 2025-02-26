package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

/**
 * Note: ES data source
 */
public class ExchangisESDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.ELASTICSEARCH;
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


}