package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Subscribe the task cached in memory(queue)
 */
public abstract class CacheInTaskObserver extends AbstractTaskObserver {

    private static final Logger LOG = LoggerFactory.getLogger(CacheInTaskObserver.class);

    protected Queue<LaunchableExchangisTask> queue;

    private static final CommonVars<Integer>  TASK_OBSERVER_CACHE_SIZE = CommonVars.apply("wds.exchangis.job.observer.cache.size", 3000);

    public CacheInTaskObserver(int cacheSize){
        this.queue = new ArrayBlockingQueue<>(cacheSize);
    }

    public CacheInTaskObserver(){
        this.queue = new ArrayBlockingQueue<>(TASK_OBSERVER_CACHE_SIZE.getValue());
    }
    @Override
    public List<LaunchableExchangisTask> onPublish(int batchSize) throws ExchangisTaskObserverException {
        List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>(batchSize);
        LaunchableExchangisTask polledTask;
        while (launchableExchangisTasks.size() < batchSize && (polledTask = queue.poll()) != null){
            launchableExchangisTasks.add(polledTask);
        }
        int fetchTaskSize = launchableExchangisTasks.size();
        int restBatchSize = batchSize - fetchTaskSize;
        if (restBatchSize > 0 && (this.lastPublishTime + this.publishInterval < System.currentTimeMillis())) {
            launchableExchangisTasks.addAll(onPublishNext(restBatchSize));
        }
        return launchableExchangisTasks;
    }

    @Override
    protected List<LaunchableExchangisTask> choose(List<LaunchableExchangisTask> candidateTasks,
                                                   TaskChooseRuler chooseRuler, TaskScheduler taskScheduler) {
        List<LaunchableExchangisTask> chooseTasks = chooseRuler.choose(candidateTasks, taskScheduler);
        // Left the rejected tasks
        candidateTasks.removeAll(chooseTasks);
        // Update the lastUpdateTime
        Date currentTime = Calendar.getInstance().getTime();
        candidateTasks.forEach(task -> task.setLastUpdateTime(currentTime));
        // Requeue into
        this.queue.addAll(candidateTasks);
        return chooseTasks;
    }

    protected abstract List<LaunchableExchangisTask> onPublishNext(int batchSize) throws ExchangisTaskObserverException;
    /**
     * Offer operation for service to add/offer queue
     * @return queue
     */
    public Queue<LaunchableExchangisTask> getCacheQueue(){
        return new OperateLimitQueue(this.queue);
    }

    /**
     * Limit the operation
     */
    private class OperateLimitQueue extends AbstractQueue<LaunchableExchangisTask>{

        private Queue<LaunchableExchangisTask> innerQueue;

        public OperateLimitQueue(Queue<LaunchableExchangisTask> queue){
            this.innerQueue = queue;
        }

        @Override
        public Iterator<LaunchableExchangisTask> iterator() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'iterator'", null);
        }

        @Override
        public int size() {
            return this.innerQueue.size();
        }

        @Override
        public boolean offer(LaunchableExchangisTask launchableExchangisTask) {
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
        public LaunchableExchangisTask poll() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'poll'", null);
        }

        @Override
        public LaunchableExchangisTask peek() {
            throw new ExchangisTaskObserverException.Runtime("Unsupported operation 'peek'", null);
        }
    }
}
