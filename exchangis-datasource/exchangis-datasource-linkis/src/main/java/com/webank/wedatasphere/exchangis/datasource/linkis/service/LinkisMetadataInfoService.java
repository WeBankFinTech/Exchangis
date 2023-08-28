package com.webank.wedatasphere.exchangis.datasource.linkis.service;

import com.webank.wedatasphere.exchangis.common.EnvironmentUtils;
import com.webank.wedatasphere.exchangis.datasource.core.domain.MetaColumn;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.request.MetadataGetConnInfoAction;
import com.webank.wedatasphere.exchangis.datasource.linkis.request.MetadataGetPartitionPropsAction;
import com.webank.wedatasphere.exchangis.datasource.linkis.response.MetadataGetConnInfoResult;
import com.webank.wedatasphere.exchangis.datasource.linkis.response.MetadataGetPartitionPropsResult;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceOperation;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceRpcDispatcher;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.MetadataGetColumnsAction;
import org.apache.linkis.datasource.client.request.MetadataGetPartitionsAction;
import org.apache.linkis.datasource.client.request.MetadataGetTablePropsAction;
import org.apache.linkis.datasource.client.response.MetadataGetColumnsResult;
import org.apache.linkis.datasource.client.response.MetadataGetPartitionsResult;
import org.apache.linkis.datasource.client.response.MetadataGetTablePropsResult;
import org.apache.linkis.metadata.query.common.domain.MetaColumnInfo;

import java.util.*;

import static com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode.*;

/**
 * Linkis to fetch metadata info
 */
public class LinkisMetadataInfoService extends LinkisDataSourceServiceRpcDispatcher<LinkisMetaDataRemoteClient>
        implements MetadataInfoService {
    // TODO define in properties file
    private static final String LOCAL_HDFS_NAME = ".LOCAL_HDFS";
    @Override
    public Class<?> getClientClass() {
        return LinkisMetaDataRemoteClient.class;
    }

    @Override
    public ServiceRpcClient<LinkisMetaDataRemoteClient> getDefaultRemoteClient() {
        return ExchangisLinkisRemoteClient::getLinkisMetadataRemoteClient;
    }

    @Override
    public Map<String, String> getPartitionProps(String userName, Long dataSourceId,
                                                 String database, String table, String partition) throws ExchangisDataSourceException {
        return getPartitionProps(getDefaultRemoteClient(), userName, dataSourceId, database, table, partition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getPartitionProps(ServiceRpcClient<?> rpcClient,
                                                 String userName, Long dataSourceId,
                                                 String database, String table, String partition) throws ExchangisDataSourceException {
        MetadataGetPartitionPropsResult result = dispatch((ServiceRpcClient<LinkisMetaDataRemoteClient>) rpcClient, new LinkisDataSourceServiceOperation(() -> {
            MetadataGetPartitionPropsAction action = new MetadataGetPartitionPropsAction(dataSourceId,
                    database, table, partition, LINKIS_RPC_CLIENT_SYSTEM.getValue());
            action.setUser(userName);
            return action;
        }), CLIENT_METADATA_GET_PARTITION_PROPS.getCode(), "getPartitionProps");
        return result.props();
    }

    @Override
    public Map<String, String> getTableProps(String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException {
        return getTableProps(getDefaultRemoteClient(), userName, dataSourceId, database, table);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getTableProps(ServiceRpcClient<?> rpcClient, String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException {
        MetadataGetTablePropsResult result = dispatch((ServiceRpcClient<LinkisMetaDataRemoteClient>) rpcClient, new LinkisDataSourceServiceOperation(() -> MetadataGetTablePropsAction.builder()
                .setDataSourceId(dataSourceId).setDatabase(database).setTable(table)
                .setUser(userName).setSystem(LINKIS_RPC_CLIENT_SYSTEM.getValue()).build()), CLIENT_METADATA_GET_TABLES_ERROR.getCode(), "getTableProps");
        return result.props();
}

    @Override
    public List<String> getPartitionKeys(String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException {
        MetadataGetPartitionsResult result = dispatch(getDefaultRemoteClient(), new LinkisDataSourceServiceOperation(() -> MetadataGetPartitionsAction.builder()
                .setDataSourceId(dataSourceId).setDatabase(database).setTable(table)
                .setUser(userName).setSystem(LINKIS_RPC_CLIENT_SYSTEM.getValue()).build()), CLIENT_METADATA_GET_PARTITION.getCode(), "getPartitionKeys");
        return result.getPartitionInfo().getPartKeys();
    }

    @Override
    public List<MetaColumn> getColumns(String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException {
        MetadataGetColumnsResult result = dispatch(getDefaultRemoteClient(), new LinkisDataSourceServiceOperation(() -> MetadataGetColumnsAction.builder()
                .setSystem(LINKIS_RPC_CLIENT_SYSTEM.getValue())
                .setDataSourceId(dataSourceId).setDatabase(database).setTable(table)
                .setUser(userName).build()),CLIENT_METADATA_GET_PARTITION.getCode(), "getColumns");
        List<MetaColumnInfo> columnInfoList = result.getAllColumns();
        List<MetaColumn> columns = new ArrayList<>();
        Optional.ofNullable(columnInfoList).ifPresent(infoList -> infoList.forEach(info ->
                columns.add(new MetaColumn(info.getIndex(), info.getName(), info.getType(), info.isPrimaryKey()))));
        return columns;
    }

    @Override
    public Map<String, String> getLocalHdfsInfo(String uri) throws ExchangisDataSourceException{
        Map<String, Object> query = new HashMap<>();
        query.put("uri", uri);
        MetadataGetConnInfoResult result = dispatch(getDefaultRemoteClient(), new LinkisDataSourceServiceOperation(() -> {
            MetadataGetConnInfoAction action = new MetadataGetConnInfoAction(LOCAL_HDFS_NAME, LINKIS_RPC_CLIENT_SYSTEM.getValue(), query);
            action.setUser(EnvironmentUtils.getJvmUser());
            return action;
        }), CLIENT_METADATA_GET_PARTITION.getCode(), "getLocalHdfsInfo");
        return result.getInfo();
    }


}
