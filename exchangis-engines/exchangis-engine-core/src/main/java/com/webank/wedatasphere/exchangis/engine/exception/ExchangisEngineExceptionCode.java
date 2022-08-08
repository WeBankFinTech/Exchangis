package com.webank.wedatasphere.exchangis.engine.exception;

/**
 * Exception code fo engine
 * 32000 ~ 32999
 */
public enum ExchangisEngineExceptionCode {
    RESOURCE_LOAD_ERROR(32000);

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
