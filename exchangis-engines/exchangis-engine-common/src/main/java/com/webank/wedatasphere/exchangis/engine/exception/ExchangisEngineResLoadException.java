package com.webank.wedatasphere.exchangis.engine.exception;

import static com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineExceptionCode.RESOURCE_LOAD_ERROR;

/**
 * Engine resource load exception
 */
public class ExchangisEngineResLoadException extends ExchangisEngineResException {


    public ExchangisEngineResLoadException(String desc) {
        super(desc);
    }

    public ExchangisEngineResLoadException(String desc, Throwable t) {
        super(desc, t);
        super.setErrCode(RESOURCE_LOAD_ERROR.getCode());
    }
}
