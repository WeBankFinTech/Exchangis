package com.webank.wedatasphere.exchangis.datasource.core.exception;


import org.apache.linkis.common.exception.ErrorException;

public class ExchangisDataSourceException extends ErrorException {

    public ExchangisDataSourceException(int errCode, String desc) {
        super(errCode, desc);
    }

    public ExchangisDataSourceException(int errCode, String desc, Throwable t) {
        super(errCode, desc);
        super.initCause(t);
    }
    public ExchangisDataSourceException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }

}