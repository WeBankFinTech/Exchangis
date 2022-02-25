
package com.webank.wedatasphere.exchangis.job.server.exception;


import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;


public class ExchangisJobServerException extends ErrorException {

    public ExchangisJobServerException(int errCode, String desc) {
        super(errCode, desc);
    }

    public ExchangisJobServerException(int errorCode, String desc, Throwable throwable) {
        super(errorCode, desc);
        this.initCause(throwable);
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
