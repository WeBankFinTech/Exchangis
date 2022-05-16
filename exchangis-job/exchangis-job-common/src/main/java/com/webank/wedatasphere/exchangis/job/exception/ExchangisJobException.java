package com.webank.wedatasphere.exchangis.job.exception;


import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.JOB_EXCEPTION_CODE;

/**
 * Exchangis Job Exception
 */
public class ExchangisJobException extends ErrorException {

    public ExchangisJobException(int errCode, String desc) {
        super(errCode, desc);
    }

    public ExchangisJobException(String desc, Throwable t){
        this(JOB_EXCEPTION_CODE.getCode(), desc, t);
    }
    public ExchangisJobException(int errCode, String desc, Throwable t) {
        super(errCode, desc);
        this.initCause(t);
    }

    public ExchangisJobException(int errCode, String desc, String ip, int port, String serviceKind) {
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
