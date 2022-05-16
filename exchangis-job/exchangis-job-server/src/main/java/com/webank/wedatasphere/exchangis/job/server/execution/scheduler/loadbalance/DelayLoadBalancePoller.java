package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * To poll element with delay timing property
 */
public abstract class DelayLoadBalancePoller<T> implements LoadBalancePoller<T>{

    private static final int MAX_POLL_SIZE = 50;

    /**
     * Delay queue
     */
    private DelayQueue<DelayElement> delayQueue = new DelayQueue<>();
    @Override
    public List<T> poll() throws InterruptedException {
        List<DelayElement> delayElements = new ArrayList<>();
        delayQueue.drainTo(delayElements, MAX_POLL_SIZE);
        while (delayElements.isEmpty()){
            try {
                DelayElement element = delayQueue.poll(3, TimeUnit.SECONDS);
                if (Objects.nonNull(element)){
                    delayElements.add(element);
                }
            } catch (InterruptedException e) {
                throw new InterruptedException();
            }
        }
        return delayElements.stream().map(delayElement -> delayElement.element).collect(Collectors.toList());
    }

    @Override
    public void push(T element) {
        DelayElement delayElement = new DelayElement(element);
        delayQueue.offer(delayElement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void combine(LoadBalancePoller<T> other) {
        // Only combine with DelayLoadBalancePoller
        if(other instanceof DelayLoadBalancePoller){
            DelayLoadBalancePoller<T> poller = (DelayLoadBalancePoller<T>)other;
            for(Object delayElement : poller.delayQueue.toArray()){
                delayQueue.put((DelayElement) delayElement);
            }
        }
    }

    @Override
    public int size() {
        return delayQueue.size();
    }

    /**
     * Get the delay time from element
     * @param element element
     * @return timestamp
     */
    protected abstract long getDelayTimeInMillis(T element);

    private class DelayElement implements Delayed{
        T element;

        private long triggerTime;

        DelayElement(T element){
            this.element = element;
            this.triggerTime = getDelayTimeInMillis(element);
        }
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.triggerTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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
