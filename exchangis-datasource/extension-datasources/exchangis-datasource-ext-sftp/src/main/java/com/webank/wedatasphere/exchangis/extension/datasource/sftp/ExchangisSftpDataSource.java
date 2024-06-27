package com.webank.wedatasphere.exchangis.extension.datasource.sftp;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

public class ExchangisSftpDataSource extends ExchangisBatchDataSource {

    @Override
    protected ExchangisDataSourceType type() {
        return ExchangisDataSourceType.SFTP;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.NON_STRUCTURED.name;
    }

    @Override
    public String description() {
        return "This is sftp";
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
        return super.getDataSourceParamConfigs(ExchangisDataSourceType.SFTP.name);
    }

}
