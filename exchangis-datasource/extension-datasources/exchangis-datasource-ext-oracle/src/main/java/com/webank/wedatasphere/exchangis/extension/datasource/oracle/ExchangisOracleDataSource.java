package com.webank.wedatasphere.exchangis.extension.datasource.oracle;

import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

/**
 * @author jefftlin
 * @create 2022-09-14
 **/
public class ExchangisOracleDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.ORACLE;
    }

    @Override
    public String description() {
        return "This is Oracle DataSource";
    }

    @Override
    public String option() {
        return "Oracle数据库";
    }


    @Override
    public String structClassifier() {
        return StructClassifier.STRUCTURED.name;
    }

    @Override
    public String icon() {
        return "icon-oracle";
    }


}
