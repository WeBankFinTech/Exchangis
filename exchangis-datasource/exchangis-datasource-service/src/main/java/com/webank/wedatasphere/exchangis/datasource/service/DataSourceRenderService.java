package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;

public interface DataSourceRenderService {

    /**
     * Get the partition info and render to element
     * @param userName userName
     * @return element ui
     */
    ElementUI<?> getPartitionAndRender(String userName,
                                       Long dataSourceId, String database, String table, ElementUI.Type uiType) throws ExchangisDataSourceException;

}
