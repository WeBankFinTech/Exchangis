package com.webank.wedatasphere.exchangis.extension.datasource.hive;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.client.DataSourceRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.client.MetaDataRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;

import java.util.List;

public class ExchangisHiveDataSource implements ExchangisDataSource {
    @Override
    public String type() {
        return "HIVE";
    }

    @Override
    public List<ElementUI> getDataSourceParamsUI() {
        return null;
    }

    @Override
    public List<ElementUI> getTransForm() {
        return null;
    }

    @Override
    public DataSourceRemoteClient getDataSourceRemoteClient() {
        return new HiveDataSourceRemoteClient();
    }

    @Override
    public MetaDataRemoteClient getMetaDataRemoteClient() {
        return null;
    }
}
