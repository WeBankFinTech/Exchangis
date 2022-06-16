package com.webank.wedatasphere.exchangis.extension.datasource.sftp;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisSftpDataSource extends ExchangisBatchDataSource {
    private static final String DATASOURCE_TYPE = "SFTP";

    @Override
    public String name() {
        return DATASOURCE_TYPE;
    }

    @Override
    public String description() {
        return "sftp连接";
    }

    @Override
    public String option() {
        return "SFTP";
    }

    @Override
    public String classifier() {
        return "SFTP";
    }

    @Override
    public String icon() {
        return "icon-sftp";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DATASOURCE_TYPE);
    }
}
