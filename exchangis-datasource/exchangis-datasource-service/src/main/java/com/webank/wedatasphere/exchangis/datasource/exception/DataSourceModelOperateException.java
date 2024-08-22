package com.webank.wedatasphere.exchangis.datasource.exception;

public class DataSourceModelOperateException extends RuntimeException {

    public DataSourceModelOperateException(Throwable cause) {
        super(cause);
    }

    public DataSourceModelOperateException(String message) {
        super(message);
    }

    public DataSourceModelOperateException(String message, Throwable cause) {
        super(message, cause);
    }
}
