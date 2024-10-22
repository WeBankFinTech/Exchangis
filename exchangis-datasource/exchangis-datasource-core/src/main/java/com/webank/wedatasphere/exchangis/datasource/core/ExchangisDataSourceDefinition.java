package com.webank.wedatasphere.exchangis.datasource.core;

import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.hook.MapperHook;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategy;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.GetAllDataSourceTypesAction;
import org.apache.linkis.datasource.client.response.GetAllDataSourceTypesResult;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Ds type definition
 */
public interface ExchangisDataSourceDefinition {

    /**
     * Type id
     * @return
     */
    String id();

    /**
     * Type name
     * @return name
     */
    String name();

    /**
     * Description
     * @return desc
     */
    String description();

    String option();

    String classifier();

    String structClassifier();

    String icon();

    /**
     * Custom split strategy
     * @return strategy
     */
    default DataSourceSplitStrategy splitStrategy(){
        return null;
    }

    /**
     * Get the split strategy
     * @return strategy
     */
    default DataSourceSplitStrategy getSplitStrategy(){
        return null;
    }
    /**
     * Split strategy name
     * @return nme
     */
    default String splitStrategyName(){
        return "";
    }

    /**
     * Split keys
     * @return array
     */
    default String[] splitKeys(){
        return new String[]{};
    }
    /**
     * Parameter config in
     * @return
     */
    default List<ExchangisJobParamConfig> getDataSourceParamConfigs(){
        return new ArrayList<>();
    };

    default LinkisDataSourceRemoteClient getDataSourceRemoteClient(){
        throw new IllegalArgumentException("unsupported to get data source remote client");
    }

    default LinkisMetaDataRemoteClient getMetaDataRemoteClient(){
        throw new IllegalArgumentException("unsupported to get metadata remote client");
    }

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
