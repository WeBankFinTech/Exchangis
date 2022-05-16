package com.webank.wedatasphere.exchangis.job.server.exception;

import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_EXECUTE_ERROR;

public class ExchangisTaskExecuteException extends ErrorException {
    public ExchangisTaskExecuteException(String desc, Throwable t) {
        super(TASK_EXECUTE_ERROR.getCode(), desc);
        super.initCause(t);
    }
    public static class Runtime extends LinkisRuntimeException {

        public Runtime(String desc, Throwable t) {
            super(TASK_EXECUTE_ERROR.getCode(), desc);
            super.initCause(t);
        }

        @Override
        public ExceptionLevel getLevel() {
            return ExceptionLevel.ERROR;
        }
    }
}
