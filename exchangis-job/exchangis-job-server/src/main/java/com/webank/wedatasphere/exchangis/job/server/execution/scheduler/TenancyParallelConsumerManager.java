package com.webank.wedatasphere.exchangis.job.server.execution.scheduler;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.scheduler.listener.ConsumerListener;
import org.apache.linkis.scheduler.queue.*;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOGroup;
import org.apache.linkis.scheduler.queue.fifoqueue.FIFOUserConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Tenancy parallel Consumer manager
 */
public class TenancyParallelConsumerManager extends ConsumerManager {

    private static final Logger LOG = LoggerFactory.getLogger(TenancyParallelConsumerManager.class);

    private ConsumerListener consumerListener;

    /**
     * Default executor service
     */
    private ExecutorService defaultExecutorService;

    private ReentrantLock defaultExecutorServiceLock = new ReentrantLock();

    private Map<String, ExecutorService> tenancyExecutorServices = new ConcurrentHashMap<>();

    private Map<String, Consumer> consumerGroupMap = new ConcurrentHashMap<>();

    private int initResidentThreads = 1;

    @Override
    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    @Override
    public ExecutorService getOrCreateExecutorService() {
        if (Objects.isNull(defaultExecutorService)){
            defaultExecutorServiceLock.lock();
            try{
                Group group = getSchedulerContext().getOrCreateGroupFactory().getOrCreateGroup(null);
                if (group instanceof FIFOGroup){
                    defaultExecutorService = Utils.newCachedThreadPool(((FIFOGroup) group).getMaxRunningJobs() +
                            this.initResidentThreads + 1,
                            TenancyParallelGroupFactory.GROUP_NAME_PREFIX + TenancyParallelGroupFactory.DEFAULT_TENANCY + "-Executor-", true);
                    tenancyExecutorServices.put(TenancyParallelGroupFactory.DEFAULT_TENANCY, defaultExecutorService);
                } else {
                    throw new ExchangisSchedulerException.Runtime("Cannot construct the executor service " +
                            "using the default group: [" + group.getClass().getCanonicalName() + "]", null);
                }
            }finally{
                defaultExecutorServiceLock.unlock();
            }
        }
        return this.defaultExecutorService;
    }

    @Override
    public Consumer getOrCreateConsumer(String groupName) {
        Consumer resultConsumer =  consumerGroupMap.computeIfAbsent(groupName, groupName0 -> {
           Consumer consumer = createConsumer(groupName);
           Group group = getSchedulerContext().getOrCreateGroupFactory().getGroup(groupName);
           consumer.setGroup(group);
           consumer.setConsumeQueue(new LoopArrayQueue(group));
           LOG.info("Create a new consumer for group: [{}]", groupName);
            Optional.ofNullable(consumerListener).ifPresent( listener -> listener.onConsumerCreated(consumer));
           consumer.start();
           return consumer;
        });
        if (resultConsumer instanceof FIFOUserConsumer){
            ((FIFOUserConsumer) resultConsumer).setLastTime(System.currentTimeMillis());
        }
        return resultConsumer;
    }

    @Override
    public Consumer createConsumer(String groupName) {
        Group group = getSchedulerContext().getOrCreateGroupFactory().getGroup(groupName);
        return new FIFOUserConsumer(getSchedulerContext(), getOrCreateExecutorService(groupName), group);
    }

    @Override
    public void destroyConsumer(String groupName) {
        Optional.ofNullable(consumerGroupMap.get(groupName)).ifPresent( consumer -> {
            LOG.warn("Start to shutdown the consumer for group: [{}]", groupName);
            consumer.shutdown();
            consumerGroupMap.remove(groupName);
            Optional.ofNullable(consumerListener).ifPresent( listener -> listener.onConsumerDestroyed(consumer));
            LOG.warn("End to shutdown the consumer for group: [{}]", groupName);
        });
    }

    /***
     * Will invoke if the spring container is down
     */
    @Override
    public void shutdown() {
        LOG.warn("Shutdown all the consumers which is working");
        consumerGroupMap.forEach((group, consumer) -> consumer.shutdown());
        LOG.warn("Shutdown all the executor service for tenancies: [{}]", StringUtils.join(tenancyExecutorServices.keySet(), ","));
        tenancyExecutorServices.forEach((tenancy, executorService) -> executorService.shutdownNow());
    }

    @Override
    public Consumer[] listConsumers() {
        return consumerGroupMap.values().toArray(new Consumer[]{});
    }

    protected ExecutorService getOrCreateExecutorService(String groupName){
        GroupFactory groupFactory =  getSchedulerContext().getOrCreateGroupFactory();
        if (groupFactory instanceof TenancyParallelGroupFactory){
            TenancyParallelGroupFactory parallelGroupFactory = (TenancyParallelGroupFactory)groupFactory;
            String tenancy = parallelGroupFactory.getTenancyByGroupName(groupName);
            groupFactory.getGroup(groupName);
            if (StringUtils.isNotBlank(tenancy)){
                return tenancyExecutorServices.computeIfAbsent(tenancy, tenancyName -> {
                    // Use the default value of max running jobs
                    return Utils.newCachedThreadPool(parallelGroupFactory.getDefaultMaxRunningJobs()  + parallelGroupFactory.getParallelPerTenancy(),
                            TenancyParallelGroupFactory.GROUP_NAME_PREFIX + tenancy + "-Executor-", true);
                });
            }
        }
        return getOrCreateExecutorService();
    }

    public int getInitResidentThreads() {
        return initResidentThreads;
    }

    public void setInitResidentThreads(int initResidentThreads) {
        this.initResidentThreads = initResidentThreads;
    }

    /**
     * Tenancy executor service
     * @return
     */
    public Map<String, ExecutorService> getTenancyExecutorServices() {
        return tenancyExecutorServices;
    }
}
