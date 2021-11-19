package com.webank.wedatasphere.exchangis.job.exception;

/**
 * Exception code, range:(31000 ~ 31999), the same as "ExchangisDataSourceExceptionCode"
 */
public enum ExchangisJobExceptionCode {
    JOB_EXCEPTION_CODE(31999),
    TRANSFORM_JOB_ERROR(31998),
    ENGINE_JOB_ERROR(31998),
    JOB_BUILDER_ERROR(31995);
    private int code;

    ExchangisJobExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
