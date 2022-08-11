package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisMongoDbDataSource extends ExchangisBatchDataSource {
    private static final String DATASOURCE_TYPE = "MONGODB";

//    @Override
//    public String id() {
//        if (null == id || id.equalsIgnoreCase("")) {
//            List<DataSourceType> types = super.getDataSourceTypes("hdfs");
//            for (DataSourceType type : types) {
//                if (type.getName().equalsIgnoreCase(DATASOURCE_TYPE)) {
//                    this.id = type.getId();
//                }
//            }
//        }
//        return this.id;
//    }

    @Override
    public String name() {
        return DATASOURCE_TYPE;
    }

    @Override
    public String classifier() {
        return "分布式全文索引";
    }

    @Override
    public String description() {
        return "ES description";
    }

    @Override
    public String option() {
        return "es无结构存储";
    }

    @Override
    public String icon() {
        return "icon-es";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DATASOURCE_TYPE);
    }
}