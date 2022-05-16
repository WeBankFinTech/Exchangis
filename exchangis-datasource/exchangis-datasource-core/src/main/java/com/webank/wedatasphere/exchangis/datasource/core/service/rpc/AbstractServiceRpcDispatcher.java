package com.webank.wedatasphere.exchangis.datasource.core.service.rpc;


import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisServiceRpcException;

import java.util.Objects;

/**
 * Abstract implements
 */
public abstract class AbstractServiceRpcDispatcher<C extends ServiceRpcClient<?>, T extends ServiceOperation>
        implements ServiceRpcDispatcher<C, T> {

    @Override
    public <U> U dispatch(T operation) throws ExchangisServiceRpcException {
        return dispatch(getDefaultRemoteClient(), operation);
    }

    @Override
    public <U> U dispatch(C remoteClient, T operation) throws ExchangisServiceRpcException {
        if (Objects.isNull(remoteClient)){
            throw new ExchangisServiceRpcException("Remote client for service: [" + this.getClass().getSimpleName() + "] cannot be empty", null);
        }
        try {
            return execute(remoteClient, operation);
        }catch(Exception e){

            if (e instanceof ExchangisServiceRpcException){
                throw e;
            }
            throw new ExchangisServiceRpcException("Unexpected exception in dispatching operation: [uri: " + operation.uri + ", timestamp: "
                    + operation.timestamp + "]", e);
        }
    }

    @Override
    public C getDefaultRemoteClient() {
        // Default client is Empty
        return null;
    }

    protected abstract <U>U execute(C remoteClient, T operation) throws ExchangisServiceRpcException;
}
