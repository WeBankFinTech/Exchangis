package com.webank.wedatasphere.exchangis.datasource.core;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.GetAllDataSourceTypesAction;
import org.apache.linkis.datasource.client.response.GetAllDataSourceTypesResult;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface ExchangisDataSource {

    String id();

    String name();

    String description();

    String option();

    String classifier();
//    String type();

    String structClassifier();

//    String category();

    String icon();

    List<ExchangisJobParamConfig> getDataSourceParamConfigs();

    LinkisDataSourceRemoteClient getDataSourceRemoteClient();

    LinkisMetaDataRemoteClient getMetaDataRemoteClient();

    void setMapperHook(MapperHook mapperHook);

    default List<DataSourceType> getDataSourceTypes(String user) {
        GetAllDataSourceTypesResult result = getDataSourceRemoteClient().getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
                .setUser(user)
                .build()
        );

        List<DataSourceType> allDataSourceType = result.getAllDataSourceType();
        if (Objects.isNull(allDataSourceType)) allDataSourceType = Collections.emptyList();
        return allDataSourceType;
    }
}
