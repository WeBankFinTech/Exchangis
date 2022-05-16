package com.webank.wedatasphere.exchangis.job.launcher.exception;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.TASK_LAUNCH_NOT_EXIST;

/**
 * Task not exist
 */
public class ExchangisTaskNotExistException extends ExchangisTaskLaunchException{
    public ExchangisTaskNotExistException(String desc, Throwable t) {
        super(desc, t);
        this.setErrCode(TASK_LAUNCH_NOT_EXIST.getCode());
    }
}
