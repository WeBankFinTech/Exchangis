package com.webank.wedatasphere.exchangis.datasource.service;

/**
 * Data source service api
 * TODO define all inf in <em>ExchangisDataSourceService</em>
 */
public interface DataSourceService {
    /**
     * Copy data source
     * @param sourceName source name
     * @param newName new name
     */
    void copyDataSource(String sourceName, String newName);
}
