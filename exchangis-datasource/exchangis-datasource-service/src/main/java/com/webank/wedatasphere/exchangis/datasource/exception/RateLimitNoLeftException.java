package com.webank.wedatasphere.exchangis.datasource.exception;

public class RateLimitNoLeftException extends RuntimeException {

    public RateLimitNoLeftException(Throwable cause) {
        super(cause);
    }

    public RateLimitNoLeftException(String message) {
        super(message);
    }

    public RateLimitNoLeftException(String message, Throwable cause) {
        super(message, cause);
    }
}
