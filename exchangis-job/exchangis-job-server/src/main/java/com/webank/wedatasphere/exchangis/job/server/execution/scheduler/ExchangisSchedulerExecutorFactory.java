package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.scheduler.executer.Executor;
import org.apache.linkis.scheduler.queue.SchedulerEvent;

/**
 * Create executor
 */
public interface ExchangisSchedulerExecutorFactory {

    /**
     * Whether create singleton executor
     * @param singleton boolean
     */
    void setIsSingleTon(boolean singleton);

    /**
     * Create Executor
     * @param event scheduler event
     * @return executor
     */
    Executor getOrCreateExecutor(SchedulerEvent event);

}
