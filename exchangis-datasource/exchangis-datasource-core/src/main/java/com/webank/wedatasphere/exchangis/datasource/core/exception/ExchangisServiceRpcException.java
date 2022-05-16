package com.webank.wedatasphere.exchangis.datasource.core.exception;

import org.apache.linkis.common.exception.ErrorException;

/**
 * Rpc exception in client
 */
public class ExchangisServiceRpcException extends ErrorException {

    public ExchangisServiceRpcException(String desc, Throwable t) {
        super(ExchangisDataSourceExceptionCode.CLIENT_RPC_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
