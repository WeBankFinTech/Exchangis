package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.DelayLoadBalancePoller;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Subscribe the task cached in memory(queue)
 */
public abstract class CacheInTaskObserver<T extends ExchangisTask> extends AbstractTaskObserver<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CacheInTaskObserver.class);

    protected OperateLimitQueue cacheQueue;

    /**
     * If limit operation
     */
    private final AtomicBoolean queueLimit = new AtomicBoolean(false);

    private static final CommonVars<Integer>  TASK_OBSERVER_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.task.observer.cache.size", 3000);

    public CacheInTaskObserver(int cacheSize){
        this.cacheQueue = new OperateLimitQueue(cacheSize,
                new DelayQueue<>());
    }

    public CacheInTaskObserver(){
       this(TASK_OBSERVER_CACHE_SIZE.getValue());
    }
    @Override
    public List<T> onPublish(String instance, int batchSize) throws ExchangisTaskObserverException {
        List<T> cacheTasks = new ArrayList<>(batchSize);
        T polledTask;
        Set<String> taskIds = new HashSet<>();
        while (cacheTasks.size() < batchSize && (polledTask = this.cacheQueue.poll()) != null){
            taskIds.add(polledTask.getId() + "");
            cacheTasks.add(polledTask);
        }
        int fetchTaskSize = cacheTasks.size();
        int restBatchSize = batchSize - fetchTaskSize;
        if (restBatchSize > 0 && (this.lastPublishTime + this.publishInterval <= System.currentTimeMillis())) {
            // Recover the queue offer entrance
            Optional.ofNullable(onPublishNext(instance, restBatchSize)).ifPresent(observerTasks -> {
                for (T observerTask : observerTasks){
                   if (taskIds.add(observerTask.getId() + "")){
                       cacheTasks.add(observerTask);
                   }
                }
                if (observerTasks.isEmpty() || observerTasks.size() < restBatchSize){
                    // Open the limit
                    if (queueLimit.get()) {
                        queueLimit.set(false);
                        LOG.info("Release the operation limit of cache queue");
                    }
                }
            });
        }
        return cacheTasks;
    }

    @Override
    protected List<T> choose(List<T> candidateTasks,
                                                   TaskChooseRuler<T> chooseRuler, Scheduler scheduler) {
        List<T> chooseTasks = chooseRuler.choose(candidateTasks, scheduler);
        // Update the lastUpdateTime
        Date currentTime = Calendar.getInstance().getTime();
        candidateTasks.forEach(task -> task.setLastUpdateTime(currentTime));
        return chooseTasks;
    }

    @Override
    protected boolean nextPublish(int publishedSize, long observerWait) {
        return publishedSize > 0;
    }

    protected abstract List<T> onPublishNext(String instance, int batchSize) throws ExchangisTaskObserverException;
    /**
     * Offer operation for service to add/offer queue
     * @return queue
     */
    public Queue<T> getCacheQueue(){
        return cacheQueue;
    }

    /**
     * Limit the operation
     */
    private class OperateLimitQueue extends AbstractQueue<T>{

        private final Queue<DelayElement> innerQueue;

        /**
         * Queue capacity
         */
        private final int capacity;
        /**
         * Queue size count
         */
        private final AtomicInteger count = new AtomicInteger();
        /**
         * Queue lock
         */
        private final ReentrantLock qLock = new ReentrantLock();


        public OperateLimitQueue(int capacity, Queue<DelayElement> queue){
            this.capacity = capacity;
            this.innerQueue = queue;
        }

        @Override
        public Iterator<T> iterator() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'iterator'", null);
        }

        @Override
        public int size() {
            return count.get();
        }

        @Override
        public boolean offer(T launchableExchangisTask) {
            qLock.lock();
            try {
                boolean offer = !queueLimit.get() && this.innerQueue.offer(
                        new DelayElement(launchableExchangisTask));
                if (offer) {
                    try {
                        publish();
                    } catch (Exception e) {
                        LOG.warn("Publish the launch-able task: {} has occurred an exception", launchableExchangisTask.getId(), e);
                    }
                    int size = count.incrementAndGet();
                    if (size >= capacity){
                        LOG.info("Limit the operation of cache queue, because reach the capacity size: [{}]", capacity);
                        queueLimit.set(true);
                    }
                }
                return offer;
            } finally {
                qLock.unlock();
            }
        }

        @Override
        public T poll() {
            qLock.lock();
            try{
                DelayElement result = this.innerQueue.poll();
                if (null != result){
                    int cnt = count.decrementAndGet();
                    if (cnt < 0){
                        count.set(0);
                    }
                    return result.element;
                }
                return null;
            }finally {
                qLock.unlock();
            }
        }

        @Override
        public T peek() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'peek'", null);
        }

    }

    /**
     * Delay element
     */
    private class DelayElement implements Delayed{
        T element;

        private final Long triggerTime;

        DelayElement(T element){
            this.element = element;
            Date delayTime = element.getDelayTime();
            if (Objects.nonNull(delayTime)){
                this.triggerTime = delayTime.getTime();
            } else {
                this.triggerTime = System.currentTimeMillis() + publishInterval;
            }

        }
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.triggerTime - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);
        }

        @Override
        @SuppressWarnings("unchecked")
        public int compareTo(Delayed o) {
            DelayElement delayElement = (DelayElement)o;
            long compare = this.triggerTime - delayElement.triggerTime;
            return compare <= 0? -1 : 1;
        }
    }
}
