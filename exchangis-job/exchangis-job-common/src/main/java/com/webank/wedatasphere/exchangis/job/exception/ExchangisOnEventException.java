package com.webank.wedatasphere.exchangis.job.exception;

import org.apache.linkis.common.exception.ErrorException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.ON_EVENT_ERROR;
/**
 * Exception happened in listener
 */
public class ExchangisOnEventException extends ErrorException {
    public ExchangisOnEventException(String desc, Throwable t) {
        super(ON_EVENT_ERROR.getCode(), desc);
    }

}
