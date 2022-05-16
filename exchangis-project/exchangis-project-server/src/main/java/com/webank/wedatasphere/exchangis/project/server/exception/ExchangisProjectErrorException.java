package com.webank.wedatasphere.exchangis.project.server.exception;


import org.apache.linkis.common.exception.ErrorException;

public class ExchangisProjectErrorException extends ErrorException {

    public ExchangisProjectErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public ExchangisProjectErrorException(int errorCode, String desc, Throwable throwable){
        super(errorCode, desc);
        this.initCause(throwable);
    }

}
