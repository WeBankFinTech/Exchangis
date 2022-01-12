package com.webank.wedatasphere.exchangis.job.server.exception;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_OBSERVER_ERROR;

/**
 * Exception in subscribing task
 */
public class ExchangisTaskObserverException extends ExchangisJobException {
    public ExchangisTaskObserverException(String desc, Throwable t) {
        super(TASK_OBSERVER_ERROR.getCode(), desc);
        super.initCause(t);
    }

    public static class Runtime extends LinkisRuntimeException {

        public Runtime(String desc, Throwable t) {
            super(TASK_OBSERVER_ERROR.getCode(), desc);
            super.initCause(t);
        }

        @Override
        public ExceptionLevel getLevel() {
            return ExceptionLevel.ERROR;
        }
    }
}
