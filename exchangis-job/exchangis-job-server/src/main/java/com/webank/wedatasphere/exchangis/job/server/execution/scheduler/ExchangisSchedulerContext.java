package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOSchedulerContextImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains the executorManager, consumerManager and groupFactory
 */
public class ExchangisSchedulerContext extends FIFOSchedulerContextImpl {

    /**
     * Tenancy list
     */
    private final List<String> tenancies;

    private int maxParallelismPerUser = 1;
    public ExchangisSchedulerContext(int maxParallelismPerUser, List<String> tenancies) {
        super(Integer.MAX_VALUE);
        this.maxParallelismPerUser = maxParallelismPerUser;
        this.tenancies = tenancies;
    }

    @Override
    public GroupFactory createGroupFactory() {
        TenancyParallelGroupFactory parallelGroupFactory = new TenancyParallelGroupFactory();
        parallelGroupFactory.setParallelPerTenancy(maxParallelismPerUser);
        parallelGroupFactory.setTenancies(this.tenancies);
        return parallelGroupFactory;
    }

    @Override
    public ConsumerManager createConsumerManager() {
        throw new ExchangisSchedulerException.Runtime("Must set the consumer manager before scheduling", null);
    }

    public List<String> getTenancies() {
        return tenancies;
    }
}
