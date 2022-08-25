package com.webank.wedatasphere.exchangis.extension.datasource.sftp;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisSftpDataSource extends ExchangisBatchDataSource {

    @Override
    public String name() {
        return DataSourceType.SFTP.name;
    }

    @Override
    public String classifier() {
        return Classifier.SFTP.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.NON_STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is Sftp";
    }

    @Override
    public String option() {
        return "SFTP";
    }

    @Override
    public String icon() {
        return "icon-sftp";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.SFTP.name);
    }
}
