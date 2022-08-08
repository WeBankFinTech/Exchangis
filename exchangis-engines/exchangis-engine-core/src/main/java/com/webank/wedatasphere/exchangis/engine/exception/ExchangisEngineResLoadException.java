package com.webank.wedatasphere.exchangis.engine.exception;

import org.apache.linkis.common.exception.ErrorException;

/**
 * Engine resource load exception
 */
public class ExchangisEngineResLoadException extends ErrorException {
    public ExchangisEngineResLoadException(String desc) {
        super(ExchangisEngineExceptionCode.RESOURCE_LOAD_ERROR.getCode(), desc);
    }

    public ExchangisEngineResLoadException(String desc, String ip, int port, String serviceKind) {
        super(ExchangisEngineExceptionCode.RESOURCE_LOAD_ERROR.getCode(), desc, ip, port, serviceKind);
    }
}
