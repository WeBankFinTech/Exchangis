package com.webank.wedatasphere.exchangis.engine.exception;

import org.apache.linkis.common.exception.ErrorException;

import static com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineExceptionCode.RESOURCE_ERROR;

/**
 * Engine resource exception
 */
public class ExchangisEngineResException extends ErrorException {
    public ExchangisEngineResException(String desc) {
        this(desc, null);
    }

    public ExchangisEngineResException(String desc, Throwable t){
        super(RESOURCE_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
