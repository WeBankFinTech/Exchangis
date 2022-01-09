package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.server.execution.TaskGeneratorContext;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOSchedulerContextImpl;

public class SchedulerTaskGeneratorContext extends FIFOSchedulerContextImpl implements TaskGeneratorContext {
    public SchedulerTaskGeneratorContext(int maxParallelismUsers) {
        super(maxParallelismUsers);
    }
}
