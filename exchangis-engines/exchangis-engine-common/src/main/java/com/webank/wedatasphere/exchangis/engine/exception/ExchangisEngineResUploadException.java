package com.webank.wedatasphere.exchangis.engine.exception;

import static com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineExceptionCode.RESOURCE_UPLOAD_ERROR;

/**
 * Engine resource upload exception
 */
public class ExchangisEngineResUploadException extends ExchangisEngineResException{
    public ExchangisEngineResUploadException(String desc) {
        super(desc);
    }

    public ExchangisEngineResUploadException(String desc, Throwable t) {
        super(desc, t);
        super.setErrCode(RESOURCE_UPLOAD_ERROR.getCode());
    }
}
