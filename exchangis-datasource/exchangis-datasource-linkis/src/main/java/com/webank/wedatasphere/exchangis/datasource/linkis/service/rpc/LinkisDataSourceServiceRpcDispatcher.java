package com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.AbstractServiceRpcDispatcher;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import org.apache.linkis.datasource.client.RemoteClient;
import org.apache.linkis.httpclient.request.Action;

public class LinkisDataSourceServiceRpcDispatcher<T extends RemoteClient> extends
        AbstractServiceRpcDispatcher<ServiceRpcClient<T>, LinkisDataSourceServiceOperation> {

    @Override
    @SuppressWarnings("unchecked")
    protected <U> U execute(ServiceRpcClient<T> remoteClient, LinkisDataSourceServiceOperation operation) throws ExchangisServiceRpcException {
        Action action = operation.getRequestAction();
        try {
            return (U) remoteClient.getClient().execute(action);
        }catch(ClassCastException e){
            throw new ExchangisServiceRpcException("The return of dispatcher should be suitable Result type", e);
        }
    }
}
