package com.webank.wedatasphere.exchangis.job.launcher.domain.task;

/**
 * Status of task
 */
public enum TaskStatus {
    Inited, Scheduled, Running, WaitForRetry, Cancelled, Failed, Success, Undefined, Timeout;

    /**
     * Is completed status
     * @param status status
     * @return boolean
     */
    public static boolean isCompleted(TaskStatus status){
        return null == status || status.equals(Cancelled) || status.equals(Failed) || status.equals(Success);
    }
}
