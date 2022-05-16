package com.webank.wedatasphere.exchangis.datasource.core.service.rpc;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;

/**
 * Dispatch the operation with remote client
 */
public interface ServiceRpcDispatcher<C extends ServiceRpcClient<?>, T extends ServiceOperation> {

    /**
     * Dispatch entrance
     * @param operation operation
     */
    <U>U dispatch(T operation) throws ExchangisServiceRpcException;

    <U>U dispatch(C remoteClient, T operation) throws ExchangisServiceRpcException;
    /**
     * Get http client
     * @return client
     */
    C getDefaultRemoteClient();
}
