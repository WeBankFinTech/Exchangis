package com.webank.wedatasphere.exchangis.extension.datasource.hive;

import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

/**
 * Note: Hive data source
 */
public class ExchangisHiveDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.HIVE;
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

}
