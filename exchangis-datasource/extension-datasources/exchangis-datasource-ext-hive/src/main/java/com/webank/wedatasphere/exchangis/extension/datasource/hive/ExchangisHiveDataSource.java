package com.webank.wedatasphere.exchangis.extension.datasource.hive;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisHiveDataSource extends ExchangisBatchDataSource {
    private static final String DATASOURCE_TYPE = "HIVE";

//    @Override
//    public String id() {
//        return null;
//    }

    @Override
    public String name() {
        return DATASOURCE_TYPE;
    }

    @Override
    public String classifier() {
        return "大数据存储";
    }

    @Override
    public String description() {
        return "Hive Description";
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
        return super.getDataSourceParamConfigs(DATASOURCE_TYPE);
    }
}
