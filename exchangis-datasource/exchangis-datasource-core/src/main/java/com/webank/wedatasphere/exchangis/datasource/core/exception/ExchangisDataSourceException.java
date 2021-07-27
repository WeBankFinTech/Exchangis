package com.webank.wedatasphere.exchangis.datasource.core.exception;

public class ExchangisDataSourceException extends RuntimeException {

    public ExchangisDataSourceException() {
    }

    public ExchangisDataSourceException(String message) {
        super(message);
    }

    public ExchangisDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangisDataSourceException(Throwable cause) {
        super(cause);
    }

    public ExchangisDataSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
