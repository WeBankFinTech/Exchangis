package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Subscribe the task cached in memory(queue)
 */
public abstract class CacheInTaskObserver<T extends ExchangisTask> extends AbstractTaskObserver<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CacheInTaskObserver.class);

    protected Queue<T> queue;

    private static final CommonVars<Integer>  TASK_OBSERVER_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.task.observer.cache.size", 3000);

    public CacheInTaskObserver(int cacheSize){
        this.queue = new ArrayBlockingQueue<>(cacheSize);
    }

    public CacheInTaskObserver(){
        this.queue = new ArrayBlockingQueue<>(TASK_OBSERVER_CACHE_SIZE.getValue());
    }
    @Override
    public List<T> onPublish(int batchSize) throws ExchangisTaskObserverException {
        List<T> cacheTasks = new ArrayList<>(batchSize);
        T polledTask;
        while (cacheTasks.size() < batchSize && (polledTask = queue.poll()) != null){
            cacheTasks.add(polledTask);
        }
        int fetchTaskSize = cacheTasks.size();
        int restBatchSize = batchSize - fetchTaskSize;
        if (restBatchSize > 0 && (this.lastPublishTime + this.publishInterval <= System.currentTimeMillis())) {
            Optional.ofNullable(onPublishNext(restBatchSize)).ifPresent(cacheTasks::addAll);
        }
        return cacheTasks;
    }

    @Override
    protected List<T> choose(List<T> candidateTasks,
                                                   TaskChooseRuler<T> chooseRuler, Scheduler scheduler) {
        List<T> chooseTasks = chooseRuler.choose(candidateTasks, scheduler);
        // Left the rejected tasks
        candidateTasks.removeAll(chooseTasks);
        // Update the lastUpdateTime
        Date currentTime = Calendar.getInstance().getTime();
        candidateTasks.forEach(task -> task.setLastUpdateTime(currentTime));
        // Requeue into
        this.queue.addAll(candidateTasks);
        return chooseTasks;
    }

    protected abstract List<T> onPublishNext(int batchSize) throws ExchangisTaskObserverException;
    /**
     * Offer operation for service to add/offer queue
     * @return queue
     */
    public Queue<T> getCacheQueue(){
        return new OperateLimitQueue(this.queue);
    }

    /**
     * Limit the operation
     */
    private class OperateLimitQueue extends AbstractQueue<T>{

        private Queue<T> innerQueue;

        public OperateLimitQueue(Queue<T> queue){
            this.innerQueue = queue;
        }

        @Override
        public Iterator<T> iterator() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'iterator'", null);
        }

        @Override
        public int size() {
            return this.innerQueue.size();
        }

        @Override
        public boolean offer(T launchableExchangisTask) {
            boolean offer = this.innerQueue.offer(launchableExchangisTask);
            if(offer){
                try {
                    publish();
                } catch (Exception e){
                    LOG.warn("Publish the launchable task: {} has occurred an exception", launchableExchangisTask.getId(), e);
                }
            }
            return offer;
        }

        @Override
        public T poll() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'poll'", null);
        }

        @Override
        public T peek() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'peek'", null);
        }
    }
}
