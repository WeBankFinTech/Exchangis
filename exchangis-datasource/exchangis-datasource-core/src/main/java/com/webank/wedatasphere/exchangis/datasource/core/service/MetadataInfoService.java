package com.webank.wedatasphere.exchangis.datasource.core.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;

import java.util.Map;

public interface MetadataInfoService<C> extends ServiceRpcInf<C> {

    /**
     * Get properties of partition
     * @param database database
     * @param table table
     * @param partition partition
     * @return map
     */
    Map<String, String> getPartitionProps(String userName, Long dataSourceId,
                                          String database, String table, String partition) throws ExchangisDataSourceException;

    Map<String, String> getPartitionProps(ServiceRpcClient<C> rpcClient,
                                          String userName, Long dataSourceId,
                                          String database, String table, String partition) throws ExchangisDataSourceException;

    /**
     * Get properties of table
     * @param database database
     * @param table table
     * @return map
     * @throws ExchangisDataSourceException
     */
    Map<String, String> getTableProps(String userName, Long dataSourceId,
                                      String database, String table) throws ExchangisDataSourceException;

    Map<String, String> getTableProps(ServiceRpcClient<C> rpcClient, String userName, Long dataSourceId,
                                      String database, String table) throws ExchangisDataSourceException;
}
