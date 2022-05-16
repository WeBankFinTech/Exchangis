package com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.AbstractServiceRpcDispatcher;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.datasource.client.RemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.httpclient.dws.response.DWSResult;
import org.apache.linkis.httpclient.request.Action;
import org.apache.linkis.httpclient.response.Result;

import java.util.Objects;

public class LinkisDataSourceServiceRpcDispatcher<T extends RemoteClient> extends
        AbstractServiceRpcDispatcher<ServiceRpcClient<T>, LinkisDataSourceServiceOperation> {

    public static final CommonVars<String> LINKIS_RPC_CLIENT_SYSTEM = CommonVars.apply("wds.exchangis.datasource.linkis.client.system", "exchangis");
    @Override
    @SuppressWarnings("unchecked")
    protected <U> U execute(ServiceRpcClient<T> remoteClient, LinkisDataSourceServiceOperation operation) throws ExchangisServiceRpcException {
        Action action = operation.getRequestAction();
        try {
            Result result = remoteClient.getClient().execute(action);
            if (Objects.isNull(result)){
                throw new ExchangisServiceRpcException("The return of client is empty, operation: [" + operation.getUri() + "]", null);
            }
            if (result instanceof DWSResult && ((DWSResult) result).getStatus() != 0){
                throw new ExchangisServiceRpcException("The status of result from client is: [" +
                        ((DWSResult) result).getStatus() + "], operation: [" + operation.getUri() + "]", null);
            }
            return (U)result;
        }catch(ClassCastException e){
            throw new ExchangisServiceRpcException("The return of dispatcher should be suitable Result type", e);
        }
    }

    /**
     * Dispatch with error resolver
     * @param rpcClient rpc client
     * @param operation operation
     * @param errorCode error code
     * @param method method
     * @param <R> return type
     * @return
     * @throws ExchangisDataSourceException
     */
    protected <R>R dispatch(ServiceRpcClient<T> rpcClient, LinkisDataSourceServiceOperation operation,
                          int errorCode, String method)
            throws ExchangisDataSourceException {
        try{
            return dispatch(rpcClient, operation);
        } catch (ExchangisServiceRpcException e){
            throw new ExchangisDataSourceException(errorCode, "Fail to invoke operation: [method: " + method + ", uri:" + operation.getUri() +"], reason: " + e.getMessage(), e);
        }
    }
}
