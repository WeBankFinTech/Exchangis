package org.apache.linkis.engineconnplugin.datax.exception;

import org.apache.linkis.common.exception.ErrorException;

public class DataxJobExecutionException extends ErrorException {

    public static final int ERROR_CODE = 16023;

    public DataxJobExecutionException(String message) {
        super(ERROR_CODE, message);
    }

    public DataxJobExecutionException(int errCode, String desc) {
        super(errCode, desc);
    }

    public DataxJobExecutionException(String message, Throwable e) {
        super(ERROR_CODE, message);
        this.initCause(e);
    }
}
