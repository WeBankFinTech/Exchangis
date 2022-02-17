package com.webank.wedatasphere.exchangis.datasource.linkis.service;

import com.webank.wedatasphere.exchangis.datasource.core.service.DataSourceInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceRpcDispatcher;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;

public class LinkisDataSourceInfoService extends LinkisDataSourceServiceRpcDispatcher<LinkisDataSourceRemoteClient> implements DataSourceInfoService {
    @Override
    public ServiceRpcClient<LinkisDataSourceRemoteClient> getDefaultRemoteClient() {
        return ExchangisLinkisRemoteClient::getLinkisDataSourceRemoteClient;
    }

}
