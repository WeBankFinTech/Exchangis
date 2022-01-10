package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.scheduler.executer.ExecutorManager;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOSchedulerContextImpl;

/**
 * Contains the executorManager, consumerManager and groupFactory
 */
public class ExchangisSchedulerContext extends FIFOSchedulerContextImpl {
    public ExchangisSchedulerContext(int maxParallelismPerUser, String tenancyPattern) {
        super(maxParallelismPerUser);
    }

    @Override
    public ExecutorManager getOrCreateExecutorManager() {
        return super.getOrCreateExecutorManager();
    }

    @Override
    public GroupFactory createGroupFactory() {
        return super.createGroupFactory();
    }

    @Override
    public ConsumerManager createConsumerManager() {
        return super.createConsumerManager();
    }

    @Override
    public ConsumerManager getOrCreateConsumerManager() {
        return super.getOrCreateConsumerManager();
    }
}
