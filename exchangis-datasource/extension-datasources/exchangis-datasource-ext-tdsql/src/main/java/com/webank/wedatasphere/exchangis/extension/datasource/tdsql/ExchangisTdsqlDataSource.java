package com.webank.wedatasphere.exchangis.extension.datasource.tdsql;

import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

/**
 * @author jefftlin
 * @date 2024/5/27
 */
public class ExchangisTdsqlDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.TDSQL;
    }
    @Override
    public String description() {
        return "This is tdsql DataSource";
    }

    @Override
    public String option() {
        return "Tdsql数据库";
    }

    @Override
    public String structClassifier() {
        return StructClassifier.STRUCTURED.name;
    }

    @Override
    public String icon() {
        return "icon-tdsql";
    }
}