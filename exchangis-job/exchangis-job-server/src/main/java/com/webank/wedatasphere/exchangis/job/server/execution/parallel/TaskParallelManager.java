package com.webank.wedatasphere.exchangis.job.server.execution.parallel;

/**
 * Task parallel manager
 */
public interface TaskParallelManager {
    enum Operation{
        SUBMIT
    }

    /**
     * Get parallel rule for task
     * @param name name
     * @param operation operation
     * @return rule context
     */
    TaskParallelRule getOrCreateRule(String name, Operation operation);
}
