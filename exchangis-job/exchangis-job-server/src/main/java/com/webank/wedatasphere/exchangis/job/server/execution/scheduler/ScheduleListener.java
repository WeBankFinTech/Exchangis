package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ExchangisSchedulerTask;

/**
 * Schedule listener
 * @param <T>
 */
public interface ScheduleListener<T extends ExchangisSchedulerTask> {
    /**
     * On schedule event
     * @param schedulerTask scheduler task
     */
    void onSchedule(T schedulerTask);
}
