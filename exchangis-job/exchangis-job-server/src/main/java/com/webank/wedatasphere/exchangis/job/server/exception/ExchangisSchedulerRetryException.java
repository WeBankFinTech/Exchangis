package com.webank.wedatasphere.exchangis.job.server.exception;

import org.apache.linkis.common.exception.LinkisRetryException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.SCHEDULER_ERROR;

/**
 * Exception in scheduling (could be retried in limit)
 */
public class ExchangisSchedulerRetryException extends LinkisRetryException {
    private int retryNum = 0;

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public ExchangisSchedulerRetryException(String desc, Throwable t) {
        super(SCHEDULER_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
