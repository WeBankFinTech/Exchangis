package com.webank.wedatasphere.exchangis.datasource.exception;

public class RateLimitOperationException extends RuntimeException {

    public RateLimitOperationException(Throwable cause) {
        super(cause);
    }

    public RateLimitOperationException(String message) {
        super(message);
    }

    public RateLimitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
