package com.webank.wedatasphere.exchangis.job.exception;

/**
 * Exception code, range:(31000 ~ 31999), the same as "ExchangisDataSourceExceptionCode"
 */
public enum ExchangisJobExceptionCode {
    RENDER_TRANSFORM_ERROR(31885),
    METRICS_OP_ERROR(31886),
    TASK_LAUNCH_NOT_EXIST(31887),
    TASK_LAUNCH_ERROR(31888),
    TASK_PARM_ERROR(31889),
    LOG_OP_ERROR(31990),
    TASK_EXECUTE_ERROR(31991),
    TASK_OBSERVER_ERROR(31992),
    ON_EVENT_ERROR(31993),
    SCHEDULER_ERROR(31994),
    JOB_BUILDER_ERROR(31995),
    UNSUPPORTED_TASK_LAUNCH_ENGINE(31996),
    TASK_GENERATE_ERROR(31997),
    JOB_EXCEPTION_CODE(31999),
    BUILDER_ENGINE_ERROR(31998),
    BUILDER_TRANSFORM_ERROR(31998),
    UNSUPPORTED_OPERATION(31999);
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
