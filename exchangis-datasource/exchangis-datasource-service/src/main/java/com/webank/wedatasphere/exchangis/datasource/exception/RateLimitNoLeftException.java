package com.webank.wedatasphere.exchangis.datasource.exception;

/**
 * @author jefftlin
 * @date 2024/8/15
 */
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
