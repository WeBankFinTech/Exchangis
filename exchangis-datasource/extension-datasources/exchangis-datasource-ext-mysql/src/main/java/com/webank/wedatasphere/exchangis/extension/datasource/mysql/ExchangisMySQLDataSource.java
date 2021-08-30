package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.List;

public class ExchangisMySQLDataSource extends ExchangisBatchDataSource {
    private static final String DATASOURCE_TYPE = "MYSQL";

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
        return "关系型数据库";
    }

    @Override
    public String description() {
        return "MYSQL description";
    }

    @Override
    public String option() {
        return "mysql数据库";
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