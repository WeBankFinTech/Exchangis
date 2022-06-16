package com.webank.wedatasphere.exchangis.datasource.core.exception;


import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

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

    public static class Runtime extends LinkisRuntimeException {

        public Runtime(int errCode, String desc, Throwable t) {
            super(errCode, desc);
            super.initCause(t);
        }

        @Override
        public ExceptionLevel getLevel() {
            return ExceptionLevel.ERROR;
        }
    }
}