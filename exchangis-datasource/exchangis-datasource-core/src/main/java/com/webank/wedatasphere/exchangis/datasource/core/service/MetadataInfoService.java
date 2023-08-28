package com.webank.wedatasphere.exchangis.datasource.core.service;

import com.webank.wedatasphere.exchangis.datasource.core.domain.MetaColumn;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;

import java.util.List;
import java.util.Map;

public interface MetadataInfoService extends ServiceRpcInf {

    /**
     * Get properties of partition
     * @param database database
     * @param table table
     * @param partition partition
     * @return map
     */
    Map<String, String> getPartitionProps(String userName, Long dataSourceId,
                                          String database, String table, String partition) throws ExchangisDataSourceException;

    Map<String, String> getPartitionProps(ServiceRpcClient<?> rpcClient,
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

    Map<String, String> getTableProps(ServiceRpcClient<?> rpcClient, String userName, Long dataSourceId,
                                      String database, String table) throws ExchangisDataSourceException;

    /**
     * Get partition keys
     * @param userName userName
     * @param dataSourceId data source id
     * @param database database
     * @param table table
     * @return
     * @throws ExchangisDataSourceException
     */
    List<String> getPartitionKeys(String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException;

    /**
     * Get columns
     * @param userName userName
     * @param dataSourceId data source id
     * @param database database
     * @param table table
     * @return
     * @throws ExchangisDataSourceException
     */
    List<MetaColumn> getColumns(String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException;

    /**
     * Get the default(local) hdfs information
     * @param uri uri
     * @return
     */
    Map<String, String> getLocalHdfsInfo(String uri) throws ExchangisDataSourceException;
}
