package com.webank.wedatasphere.exchangis.job.server.exception;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_OBSERVER_ERROR;

/**
 * Exception in subscribing task
 */
public class ExchangisTaskObserverException extends ExchangisJobException {

    private String methodName;
    public ExchangisTaskObserverException(String methodName, String desc, Throwable t) {
        this(desc, t);
        this.methodName = methodName;
    }
    public ExchangisTaskObserverException(String desc, Throwable t) {
        super(TASK_OBSERVER_ERROR.getCode(), desc);
        super.initCause(t);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
