package com.webank.wedatasphere.exchangis.extension.datasource.starrocks;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/5/14
 */
public class ExchangisStarRocksDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.STARROCKS.name;
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
    public String classifier() {
        return null;
    }

    @Override
    public String structClassifier() {
        return null;
    }

    @Override
    public String icon() {
        return null;
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.STARROCKS.name);
    }
}
