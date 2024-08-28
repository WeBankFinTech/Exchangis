package com.webank.wedatasphere.exchangis.datasource.exception;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import org.apache.linkis.common.exception.ErrorException;

public class RateLimitNoLeftException extends ErrorException {

    public RateLimitNoLeftException(String desc) {
        super(ExchangisDataSourceExceptionCode.RATE_LIMIT_NOT_LEFT_ERROR.getCode(), desc);
    }

    public RateLimitNoLeftException(String desc, Throwable cause) {
        super(ExchangisDataSourceExceptionCode.RATE_LIMIT_NOT_LEFT_ERROR.getCode(), desc);
        this.initCause(cause);
    }
}
