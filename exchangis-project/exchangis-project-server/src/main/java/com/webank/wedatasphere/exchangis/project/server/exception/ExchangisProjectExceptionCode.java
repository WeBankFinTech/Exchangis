package com.webank.wedatasphere.exchangis.project.server.exception;

/**
 * @author jefftlin
 * @create 2022-09-13
 **/
public enum ExchangisProjectExceptionCode {

    UNSUPPORTED_OPERATION(32001);

    private int code;

    public int getCode() {
        return code;
    }

    ExchangisProjectExceptionCode(int code) {
        this.code = code;
    }
}
