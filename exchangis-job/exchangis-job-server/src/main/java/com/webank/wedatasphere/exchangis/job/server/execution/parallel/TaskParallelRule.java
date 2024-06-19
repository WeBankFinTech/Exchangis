package com.webank.wedatasphere.exchangis.job.server.execution.parallel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task parallel rule
 */
public class TaskParallelRule {
    /**
     * Use identity as rule name, such as tenancy name and so on
     */
    private String ruleName;

    /**
     * Task operation
     */
    private TaskParallelManager.Operation operation;

    /**
     * Max parallel
     */
    private final int maxParallel;

    /**
     * Parallel
     */
    private final AtomicInteger parallel;

    public TaskParallelRule(String ruleName,
                            TaskParallelManager.Operation operation, int maxParallel) {
        this.ruleName = ruleName;
        this.operation = operation;
        parallel = new AtomicInteger();
        this.maxParallel = maxParallel;
    }

    /**
     * Increase parallel
     * @return bool
     */
    public boolean incParallel(){
        while (true) {
            int current = parallel.get();
            int next = current + 1;
            if (next > maxParallel) {
                // Reach the upper bound
                return false;
            }
            if (parallel.compareAndSet(current, next)) {
                return true;
            }
        }
    }

    /**
     * Decrease parallel with semaphore
     */
    public void decParallel(int semaphore){
        while (true) {
            int current = parallel.get();
            int next = current - semaphore;
            if (next <= 0) {
                // Reach the low bound
                next = 0;
            }
            if (parallel.compareAndSet(current, next)) {
                return;
            }
        }
    }

    public String getRuleName() {
        return ruleName;
    }

    public TaskParallelManager.Operation getOperation() {
        return operation;
    }

    public int getMaxParallel() {
        return maxParallel;
    }

    public AtomicInteger getParallel() {
        return parallel;
    }
}
