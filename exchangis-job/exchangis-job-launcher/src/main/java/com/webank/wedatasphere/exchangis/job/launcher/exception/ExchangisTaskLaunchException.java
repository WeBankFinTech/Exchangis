package com.webank.wedatasphere.exchangis.job.launcher.exception;

import org.apache.linkis.common.exception.ErrorException;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_LAUNCH_ERROR;

/**
 * Launch exception
 */
public class ExchangisTaskLaunchException extends ErrorException {
    public ExchangisTaskLaunchException(String desc, Throwable t) {
        super(TASK_LAUNCH_ERROR.getCode(), desc);
        super.initCause(t);
    }
}
