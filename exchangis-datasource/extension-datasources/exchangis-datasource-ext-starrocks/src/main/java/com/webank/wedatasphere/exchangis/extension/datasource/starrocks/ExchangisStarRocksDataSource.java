package com.webank.wedatasphere.exchangis.extension.datasource.starrocks;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/5/14
 */
public class ExchangisStarRocksDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.STARROCKS;
    }
    @Override
    public String description() {
        return "This is StarRocks DataSource";
    }

    @Override
    public String option() {
        return "StarRocks数据库";
    }

    @Override
    public String structClassifier() {
        return StructClassifier.STRUCTURED.name;
    }

    @Override
    public String icon() {
        return "icon-starrocks";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(ExchangisDataSourceType.STARROCKS.name);
    }


}
