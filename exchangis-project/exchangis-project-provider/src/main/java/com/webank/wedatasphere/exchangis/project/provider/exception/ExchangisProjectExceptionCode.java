package com.webank.wedatasphere.exchangis.project.provider.exception;

/**
 * @author jefftlin
 * @create 2022-09-13
 **/
public enum ExchangisProjectExceptionCode {

    UNSUPPORTED_OPERATION(32001),

    VALIDATE_DS_ERROR(32002),

    RELEASE_PROJECT_DS_RELATION_ERROR(32003);

    private int code;

    public int getCode() {
        return code;
    }

    ExchangisProjectExceptionCode(int code) {
        this.code = code;
    }
}
