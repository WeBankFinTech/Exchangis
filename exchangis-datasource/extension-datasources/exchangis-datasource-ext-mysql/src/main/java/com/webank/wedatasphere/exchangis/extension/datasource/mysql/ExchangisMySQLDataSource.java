package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisMySQLDataSource extends ExchangisBatchDataSource {
    private static final String DATASOURCE_TYPE = "MYSQL";

    @Override
    public String type() {
        return DATASOURCE_TYPE;
    }

    @Override
    public String description() {
        return "MYSQL description";
    }

    @Override
    public String icon() {
        return "icon-mysql";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DATASOURCE_TYPE);
    }
}