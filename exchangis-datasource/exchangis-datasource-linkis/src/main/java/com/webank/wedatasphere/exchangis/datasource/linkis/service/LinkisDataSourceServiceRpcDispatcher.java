package com.webank.wedatasphere.exchangis.datasource.linkis.service;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.AbstractServiceRpcDispatcher;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;

public class LinkisDataSourceServiceRpcDispatcher extends
        AbstractServiceRpcDispatcher<ServiceRpcClient<LinkisDataSourceRemoteClient>, LinkisDataSourceServiceOperation> {

    @Override
    protected <U> U execute(ServiceRpcClient<LinkisDataSourceRemoteClient> remoteClient, LinkisDataSourceServiceOperation operation) throws ExchangisServiceRpcException {
        return null;
    }
}
