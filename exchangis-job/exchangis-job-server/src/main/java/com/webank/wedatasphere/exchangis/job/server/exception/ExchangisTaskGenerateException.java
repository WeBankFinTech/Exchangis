package com.webank.wedatasphere.exchangis.job.server.exception;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_GENERATE_ERROR;

/**
 * Exception in generating tasks of job
 */
public class ExchangisTaskGenerateException extends ExchangisJobException {
    public ExchangisTaskGenerateException(String desc, Throwable t) {
        super(TASK_GENERATE_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
