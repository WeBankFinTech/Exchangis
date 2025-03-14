package com.webank.wedatasphere.exchangis.datasource.exception;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import org.apache.linkis.common.exception.ErrorException;

public class RateLimitOperationException extends ErrorException {

    public RateLimitOperationException(String desc) {
        super(ExchangisDataSourceExceptionCode.RATE_LIMIT_OPERATION_ERROR.getCode(), desc);
    }

    public RateLimitOperationException(String desc, Throwable cause) {
        super(ExchangisDataSourceExceptionCode.RATE_LIMIT_OPERATION_ERROR.getCode(), desc);
        this.initCause(cause);
    }
}
