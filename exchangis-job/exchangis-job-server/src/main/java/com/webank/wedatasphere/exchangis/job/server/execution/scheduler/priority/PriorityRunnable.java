package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.priority;

/**
 * Runnable with priority
 */
public interface PriorityRunnable extends Runnable{

    /**
     * Default: 1
     * @return value
     */
    default int getPriority(){
        return 1;
    }

}
