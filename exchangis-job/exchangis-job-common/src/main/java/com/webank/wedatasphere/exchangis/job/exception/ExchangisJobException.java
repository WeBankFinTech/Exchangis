package com.webank.wedatasphere.exchangis.job.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.JOB_EXCEPTION_CODE;

/**
 * Exchangis Job Exception
 */
public class ExchangisJobException extends ErrorException {
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
}
