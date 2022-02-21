package com.webank.wedatasphere.exchangis.datasource.linkis.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.request.MetadataGetPartitionPropsAction;
import com.webank.wedatasphere.exchangis.datasource.linkis.response.MetadataGetPartitionPropsResult;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceOperation;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceRpcDispatcher;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.MetadataGetTablePropsAction;
import org.apache.linkis.datasource.client.response.MetadataGetTablePropsResult;

import java.util.Map;

import static com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_PARTITION_PROPS;

/**
 * Linkis to fetch metadata info
 */
public class LinkisMetadataInfoService extends LinkisDataSourceServiceRpcDispatcher<LinkisMetaDataRemoteClient>
        implements MetadataInfoService {

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
    public Map<String, String> getTableProps(ServiceRpcClient<?> rpcClient, String userName, Long dataSourceId, String database, String table) throws ExchangisDataSourceException {
        MetadataGetTablePropsResult result = dispatch((ServiceRpcClient<LinkisMetaDataRemoteClient>) rpcClient, new LinkisDataSourceServiceOperation(() -> MetadataGetTablePropsAction.builder()
                .setDataSourceId(dataSourceId).setDatabase(database).setTable(table)
                .setUser(userName).setSystem(LINKIS_RPC_CLIENT_SYSTEM.getValue()).build()), 0, "");
        return result.props();
    }


}
