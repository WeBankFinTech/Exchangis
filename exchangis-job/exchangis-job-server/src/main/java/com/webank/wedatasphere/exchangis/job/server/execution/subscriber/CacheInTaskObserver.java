package com.webank.wedatasphere.exchangis.job.server.execution.subscriber;


import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisTaskObserverException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.TaskScheduler;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Subscribe the task cached in memory(queue)
 */
public abstract class CacheInTaskObserver extends AbstractTaskObserver {
    protected Queue<LaunchableExchangisTask> queue;

    public CacheInTaskObserver(int batchSize, int cacheSize){
        super(batchSize);
        this.queue = new ArrayBlockingQueue<>(cacheSize);
    }

    @Override
    public List<LaunchableExchangisTask> onPublish(int batchSize) {
        List<LaunchableExchangisTask> launchableExchangisTasks = new ArrayList<>(batchSize);
        LaunchableExchangisTask polledTask;
        while (launchableExchangisTasks.size() < batchSize && (polledTask = queue.poll()) != null){
            launchableExchangisTasks.add(polledTask);
        }
        int fetchTaskSize = launchableExchangisTasks.size();
        int restBatchSize = batchSize - fetchTaskSize;
        if (restBatchSize > 0) {
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

    protected abstract List<LaunchableExchangisTask> onPublishNext(int batchSize);
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
    private static class OperateLimitQueue extends AbstractQueue<LaunchableExchangisTask>{

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
            return this.innerQueue.offer(launchableExchangisTask);
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
