package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.AbstractScheduler;
import org.apache.linkis.scheduler.SchedulerContext;
import org.apache.linkis.scheduler.executer.ExecutorManager;
import org.apache.linkis.scheduler.queue.ConsumerManager;
import org.apache.linkis.scheduler.queue.GroupFactory;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOSchedulerContextImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Inherited the AbstractScheduler from linkis-scheduler
 */
@Component
public class ExchangisTaskScheduler extends AbstractScheduler {

    private static final CommonVars<Integer> MAX_PARALLEL_PER_TENANCY = CommonVars.apply("wds.exchangis.job.scheduler.consumer.max.parallel.per-tenancy", 1);

    private static final CommonVars<String> TENANCY_PATTERN = CommonVars.apply("wds.exchangis.job.scheduler.consumer.tenancies", "hadoop");

    private static final CommonVars<Integer> GROUP_INIT_CAPACITY = CommonVars.apply("wds.exchangis.job.scheduler.group.min.capacity", 1000);

    private static final CommonVars<Integer> GROUP_MAX_CAPACITY = CommonVars.apply("wds.exchangis.job.scheduler.group.max.capacity", 5000);

    private static final CommonVars<Integer> GROUP_MAX_RUNNING_JOBS = CommonVars.apply("wds.exchangis.job.scheduler.group.max.running-jobs", 30);
    private SchedulerContext schedulerContext;

    @Resource
    private ExecutorManager executorManager;

    @Resource
    private ConsumerManager consumerManager;

    @Override
    @PostConstruct
    public void init() {
        this.schedulerContext = new ExchangisSchedulerContext(MAX_PARALLEL_PER_TENANCY.getValue(), TENANCY_PATTERN.getValue());
        GroupFactory groupFactory = this.schedulerContext.getOrCreateGroupFactory();
        if (groupFactory instanceof TenancyParallelGroupFactory){
            TenancyParallelGroupFactory tenancyParallelGroupFactory = (TenancyParallelGroupFactory)groupFactory;
            tenancyParallelGroupFactory.setDefaultInitCapacity(GROUP_INIT_CAPACITY.getValue());
            tenancyParallelGroupFactory.setDefaultMaxCapacity(GROUP_MAX_CAPACITY.getValue());
            tenancyParallelGroupFactory.setDefaultMaxRunningJobs(GROUP_MAX_RUNNING_JOBS.getValue());
        }
        ((FIFOSchedulerContextImpl) this.schedulerContext).setExecutorManager(executorManager);
        ((FIFOSchedulerContextImpl) this.schedulerContext).setConsumerManager(consumerManager);
    }

    @Override
    public String getName() {
        return "Exchangis-Multi-Tenancy-Scheduler";
    }

    @Override
    public SchedulerContext getSchedulerContext() {
        return schedulerContext;
    }
}
