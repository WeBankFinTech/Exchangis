package com.webank.wedatasphere.exchangis.privilege.exception;

public enum ExchangisPrivilegeExceptionCode {

    FETCH_PROXY_USER_ERROR(41000),
    ;

    private int code;

    ExchangisPrivilegeExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
