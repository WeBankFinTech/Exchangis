package com.webank.wedatasphere.exchangis.extension.datasource.oracle;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.domain.Classifier;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceType;
import com.webank.wedatasphere.exchangis.datasource.core.domain.StructClassifier;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisBatchDataSource;

import java.util.List;

/**
 * @author jefftlin
 * @create 2022-09-14
 **/
public class ExchangisOracleDataSource extends ExchangisBatchDataSource {
    @Override
    public String name() {
        return DataSourceType.ORACLE.name;
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
    public String classifier() {
        return Classifier.ORACLE.name;
    }

    @Override
    public String structClassifier() {
        return StructClassifier.STRUCTURED.name;
    }

    @Override
    public String icon() {
        return "icon-oracle";
    }

    @Override
    public List<ExchangisJobParamConfig> getDataSourceParamConfigs() {
        return super.getDataSourceParamConfigs(DataSourceType.ORACLE.name);
    }

}
