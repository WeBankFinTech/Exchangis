package com.webank.wedatasphere.exchangis.privilege.exception;

import org.apache.linkis.common.exception.ErrorException;

public class ExchangisPrivilegeException extends ErrorException {

    public ExchangisPrivilegeException(String desc) {
        super(ExchangisPrivilegeExceptionCode.FETCH_PROXY_USER_ERROR.getCode(), desc);
    }

    public ExchangisPrivilegeException(String desc, Throwable cause) {
        super(ExchangisPrivilegeExceptionCode.FETCH_PROXY_USER_ERROR.getCode(), desc);
        this.initCause(cause);
    }
}