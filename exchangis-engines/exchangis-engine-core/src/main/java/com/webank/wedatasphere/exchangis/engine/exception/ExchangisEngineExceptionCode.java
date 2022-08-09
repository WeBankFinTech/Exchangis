package com.webank.wedatasphere.exchangis.engine.exception;

/**
 * Exception code fo engine
 * 32000 ~ 32999
 */
public enum ExchangisEngineExceptionCode {
    RESOURCE_ERROR(32000),
    RESOURCE_LOAD_ERROR(32001),
    RESOURCE_UPLOAD_ERROR(32002);

    private int code;

    ExchangisEngineExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
