package com.webank.wedatasphere.exchangis.datasource.core;

import com.webank.wedatasphere.exchangis.datasource.core.client.DataSourceRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.client.MetaDataRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;

import java.util.List;

public interface ExchangisDataSource {

    String type();

    String description();

    String icon();

    List<ElementUI> getDataSourceParamsUI();

    List<ElementUI> getTransForm();

    DataSourceRemoteClient getDataSourceRemoteClient();

    MetaDataRemoteClient getMetaDataRemoteClient();

}
