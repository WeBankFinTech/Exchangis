package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.priority;

import com.webank.wedatasphere.exchangis.job.utils.SnowFlake;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Refer to 'PriorityBlockingQueue',
 * Use snowflake to generate order number of elements
 */
public class PriorityOrderedQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    /**
     * Priority queue
     */
    private final PriorityBlockingQueue<Ordered> priorityQueue;

    /**
    /**
     * Snowflake context
     */
    private final SnowFlake snowFlake;
    public PriorityOrderedQueue(int initialCapacity,
                                Comparator<? super E> comparator){
        if (Objects.isNull(comparator)){
            this.priorityQueue = new PriorityBlockingQueue<>(initialCapacity,
                    (left, right) -> (int) (right.seq - left.seq));
        } else {
            this.priorityQueue = new PriorityBlockingQueue<>(initialCapacity,
                    (left, right) -> {
                        int result = comparator.compare(left.element, right.element);
                        if (result == 0){
                            return (int)(left.seq - right.seq);
                        }
                        return result;
                    });
        }
        this.snowFlake = new SnowFlake(0, 0, System.currentTimeMillis());
    }
    @Override
    public Iterator<E> iterator() {
        return new Itr(priorityQueue.iterator());
    }

    @Override
    public int size() {
        return priorityQueue.size();
    }

    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    @Override
    public E take() throws InterruptedException {
        Ordered ordered = this.priorityQueue.take();
        return ordered.element;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        Ordered ordered = this.priorityQueue.poll(timeout, unit);
        if (null != ordered){
            return ordered.element;
        }
        return null;
    }

    @Override
    public int remainingCapacity() {
        return this.priorityQueue.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int drainTo(Collection<? super E> c, int maxElements) {
        Collection<? super Ordered> collection = null;
        if (null != c && c != this){
            collection = c.stream().map(e -> new Ordered((E) e)).collect(Collectors.toList());
        }
        return this.priorityQueue.drainTo(collection);
    }

    @Override
    public boolean offer(E e) {
        return this.priorityQueue.offer(new Ordered(e));
    }

    @Override
    public E poll() {
        Ordered ordered = this.priorityQueue.poll();
        if (null != ordered){
            return ordered.element;
        }
        return null;
    }

    @Override
    public E peek() {
        Ordered ordered = this.priorityQueue.peek();
        if (null != ordered){
            return ordered.element;
        }
        return null;
    }

    private class Ordered{
        /**
         * Seq number
         */
        private long seq;

        /**
         * Queue element
         */
        private E element;

        public Ordered(E element){
            this.seq = snowFlake.nextId();
            this.element = element;
        }
    }

    private class Itr implements Iterator<E> {
        private Iterator<Ordered> innerItr;
        public Itr(Iterator<Ordered> iterator){
            innerItr = iterator;
        }


        @Override
        public boolean hasNext() {
            return innerItr.hasNext();
        }

        @Override
        public E next() {
            return innerItr.next().element;
        }

        @Override
        public void remove() {
            innerItr.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            innerItr.forEachRemaining(eOrdered ->
                    action.accept(eOrdered.element));
        }
    }
}
