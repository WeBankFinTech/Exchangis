package com.webank.wedatasphere.exchangis.datasource.service;

import org.apache.linkis.common.exception.ErrorException;

/**
 * Data source service api
 * TODO define all inf in <em>ExchangisDataSourceService</em>
 */
public interface DataSourceService {
    /**
     * Copy data source
     * @param operator operate user
     * @param sourceName source name
     * @param newName new name
     */
    void copyDataSource(String operator,
                        String sourceName, String newName) throws ErrorException;
}
