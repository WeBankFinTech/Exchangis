package org.apache.linkis.engineconnplugin.datax.config.exception;

import org.apache.linkis.common.exception.ErrorException;

public class JobExecutionException extends ErrorException {

    public static final int ERROR_CODE = 16023;

    public JobExecutionException(String message) {
        super(ERROR_CODE, message);
    }

    public JobExecutionException(int errCode, String desc) {
        super(errCode, desc);
    }

    public JobExecutionException(String message, Throwable e) {
        super(ERROR_CODE, message);
        this.initCause(e);
    }
}
