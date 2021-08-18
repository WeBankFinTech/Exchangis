package com.webank.wedatasphere.exchangis.datasource.core;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;

import java.util.List;

public interface ExchangisDataSource {

    String type();

    String description();

    String category();

    String icon();

    List<ExchangisJobParamConfig> getDataSourceParamConfigs();

    LinkisDataSourceRemoteClient getDataSourceRemoteClient();

    LinkisMetaDataRemoteClient getMetaDataRemoteClient();

    void setMapperHook(MapperHook mapperHook);

}
