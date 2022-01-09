package com.webank.wedatasphere.exchangis.job.server.exception;

import org.apache.linkis.common.exception.ErrorException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.SCHEDULER_ERROR;

/**
 * Exception in scheduling
 */
public class ExchangisSchedulerException extends ErrorException {
    public ExchangisSchedulerException(String desc, Throwable t) {
        super(SCHEDULER_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
