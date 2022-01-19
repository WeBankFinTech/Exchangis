package com.webank.wedatasphere.exchangis.job.launcher.domain;

/**
 * Status of task
 */
public enum TaskStatus {
    Inited, Scheduled, Running, WaitForRetry, Cancelled, Failed, Success, Undefined, Timeout;
}
