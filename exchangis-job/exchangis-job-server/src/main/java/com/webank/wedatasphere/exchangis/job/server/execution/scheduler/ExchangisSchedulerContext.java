package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOSchedulerContextImpl;

import java.util.Arrays;
import java.util.Collections;

/**
 * Contains the executorManager, consumerManager and groupFactory
 */
public class ExchangisSchedulerContext extends FIFOSchedulerContextImpl {

    /**
     * Tenancy list
     */
    private String tenancies;

    private int maxParallelismPerUser = 1;
    public ExchangisSchedulerContext(int maxParallelismPerUser, String tenancies) {
        super(Integer.MAX_VALUE);
        this.maxParallelismPerUser = maxParallelismPerUser;
        if (StringUtils.isNotBlank(tenancies)){
            this.tenancies = tenancies;
        }
    }

    @Override
    public GroupFactory createGroupFactory() {
        TenancyParallelGroupFactory parallelGroupFactory = new TenancyParallelGroupFactory();
        parallelGroupFactory.setParallelPerTenancy(maxParallelismPerUser);
        parallelGroupFactory.setTenancies(StringUtils.isNotBlank(tenancies)? Arrays.asList(tenancies.split(",")) : Collections.emptyList());
        return parallelGroupFactory;
    }

    @Override
    public ConsumerManager createConsumerManager() {
        throw new ExchangisSchedulerException.Runtime("Must set the consumer manager before scheduling", null);
    }

}
