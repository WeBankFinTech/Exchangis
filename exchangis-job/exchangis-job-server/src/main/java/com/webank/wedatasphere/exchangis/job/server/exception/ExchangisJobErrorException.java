
package com.webank.wedatasphere.exchangis.job.server.exception;


import org.apache.linkis.common.exception.ErrorException;

public class ExchangisJobErrorException extends ErrorException {

    public ExchangisJobErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public ExchangisJobErrorException(int errorCode, String desc, Throwable throwable) {
        super(errorCode, desc);
        this.initCause(throwable);
    }

}
