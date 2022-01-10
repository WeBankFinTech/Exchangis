package com.webank.wedatasphere.exchangis.job.exception;

/**
 * Exception code, range:(31000 ~ 31999), the same as "ExchangisDataSourceExceptionCode"
 */
public enum ExchangisJobExceptionCode {
    SCHEDULER_ERROR(31994),
    JOB_BUILDER_ERROR(31995),
    UNSUPPORTED_JOB_EXECUTION_ENGINE(31996),
    TASK_GENERATE_ERROR(31997),
    JOB_EXCEPTION_CODE(31999),
    ENGINE_JOB_ERROR(31998),
    TRANSFORM_JOB_ERROR(31998);
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
