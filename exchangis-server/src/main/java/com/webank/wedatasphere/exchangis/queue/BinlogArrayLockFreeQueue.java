package com.webank.wedatasphere.exchangis.queue;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class BinlogArrayLockFreeQueue<T> {
    /**
     * Allocation 64 MB buffer default
     */
    private static final int DEFAULT_QUEUE_BUFFER = 1024 * 1024 * 64;

    private static final long DEFAULT_MEASURE_INTERVAL = 2 * 1000L;

    /**
     * We should reduce the cpu usage
     */
    private static final long DEFAULT_SPIN_TIMES = 10;

    private final Unsafe unsafe = UnsafeUtil.unsafe;

    final Object[] items;

    /**
     * take index
     */
    private volatile long takeIndex;

    /**
     * put index
     */
    private volatile long putIndex;

    /**
     * max take index
     */
    private volatile long maxTakeIndex;

    /**
     * Memory bytes accumulated
     */
    private volatile long memoryBytes;

    /**
     * Wait take time
     */
    private volatile long waitTake;

    /**
     * Wait put time
     */
    private volatile long waitPut;

    /**
     * Flag to measure
     */
    private volatile long measureFlag;

    /**
     * Buffer size limit
     */
    private long bufferSize;
    /**
     * Measure interval
     */
    private long measureInterval;


    private final ReentrantLock waitLock = new ReentrantLock(false);

    private final Condition notEmpty = waitLock.newCondition();

    public BinlogArrayLockFreeQueue(int capacity, long bufferSize, long measureInterval){
        // Init the array size as ring buffer, left one chunk
        if ((capacity & (capacity - 1)) != 0){
            throw new IllegalArgumentException("the value of capacity must equal to 2^N and greater than 1");
        }
        items = new Object[capacity];
        if (bufferSize <= 0){
            bufferSize = Integer.MAX_VALUE;
        }
        this.bufferSize = bufferSize;
        this.measureInterval = measureInterval;
    }

    public BinlogArrayLockFreeQueue(int capacity){
        this(capacity, DEFAULT_QUEUE_BUFFER, DEFAULT_MEASURE_INTERVAL);
    }
    
    public void put(T message) throws InterruptedException {
        if (Objects.nonNull(message)){
            long curTakeIndex;
            long curPutIndex;
            long nextPutIndex;
            long waitTime = 0;
            long clock = 0;
            try {
                do {
                    int counter = -1;
                    do  {
                        counter ++;
                        // Lock and wait the queue not full
                        if (counter > 0) {
                            LockSupport.parkNanos(1L);
                        }
                        curPutIndex = this.putIndex;
                        curTakeIndex = this.takeIndex;
                        nextPutIndex = curPutIndex + 1;
                        clock = System.nanoTime();
                    } while(toIndex(nextPutIndex) == toIndex(curTakeIndex));
                    if (counter > 0){
                        waitTime += (System.nanoTime() - clock);
                    }
                } while (!unsafe.compareAndSwapLong(this, Offsets.putIndexOffset, curPutIndex, nextPutIndex));
                // Accumulate the memory
                accumulateMemory(1);
                // Write the circle
                this.items[toIndex(curPutIndex)] = message;
//                if (waitTime > 0) {
//                    unsafe.getAndAddLong(this, Offsets.waitTakeOffset, waitTime);
//                }
                while (!unsafe.compareAndSwapLong(this, Offsets.maxTakeIndexOffset, curPutIndex, nextPutIndex)){
                    // Notify the older producer to update the max take index
                    Thread.yield();
                }

            }finally {
                // Notify the waiter
                waitLock.lock();
                try {
                    notEmpty.signalAll();
                } finally {
                    waitLock.unlock();
                }
                // Try to measure the queue indicator
//                measureIndicator();
            }
        }
    }

    
    @SuppressWarnings("unchecked")
    public T take(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        long curMaxTakeIndex;
        long curTakeIndex;
        long nextTakeIndex;
        T element;
        int takePos;
        int iterator = 0;
        long waitTime = 0;
        do {
            curMaxTakeIndex = this.maxTakeIndex;
            curTakeIndex = this.takeIndex;
            long clock = System.nanoTime();
            while (toIndex(curTakeIndex) == toIndex(curMaxTakeIndex)) {
                // Wrap as wait strategy
                ++ iterator;
                // Enable to iterator times
                if (iterator > DEFAULT_SPIN_TIMES && iterator <= DEFAULT_SPIN_TIMES * 2){
                    // Try to park to release cpu
                    LockSupport.parkNanos(1L);
                } else if (iterator > DEFAULT_SPIN_TIMES * 2){
                    waitLock.lockInterruptibly();
                    curTakeIndex = this.takeIndex;
                    curMaxTakeIndex = this.maxTakeIndex;
                    try {
                        if (toIndex(curTakeIndex) == toIndex(curMaxTakeIndex)) {
                            if (nanos <= 0) {
                                return null;
                            }
                            nanos = notEmpty.awaitNanos(nanos);
                            iterator = 0;
                        }
                    } finally {
                        waitLock.unlock();
                    }
                }
                curTakeIndex = this.takeIndex;
                curMaxTakeIndex = this.maxTakeIndex;
            }
            if (iterator > 0){
                waitTime += (System.nanoTime() - clock);
            }
            nextTakeIndex = curTakeIndex + 1;
            takePos = toIndex(curTakeIndex);
            element = (T) this.items[takePos];
        } while(!unsafe.compareAndSwapLong(this, Offsets.takeIndexOffset, curTakeIndex, nextTakeIndex));
        // Empty the cache and release the memory
        if (null != element) {
            this.items[takePos] = null;
//            unsafe.getAndAddInt(this, Offsets.memoryBytesOffset, -1);
        }
//        if (waitTime > 0){
//            unsafe.getAndAddLong(this, Offsets.waitPutOffset, waitTime);
//        }
        this.items[takePos] = null;
        // Try to measure the queue indicator
        measureIndicator();
        return element;
    }

    
    @SuppressWarnings("unchecked")
    public int drainTo(List<T> elements, int maxElements) {
        long curMaxTakeIndex = this.maxTakeIndex;
        long curTakeIndex = this.takeIndex;
        long nextTakeIndex;
        int takePos;
        int count = 0;
        int bytesCnt = 0;
        // Break if queue is empty
        while(toIndex(curTakeIndex) != toIndex(curMaxTakeIndex)) {
            nextTakeIndex = curTakeIndex + 1;
            takePos = toIndex(curTakeIndex);
            if (unsafe.compareAndSwapLong(this, Offsets.takeIndexOffset, curTakeIndex, nextTakeIndex)){
                T element = (T) this.items[takePos];
                elements.add(element);
                count ++;
                // Empty the cache
                this.items[takePos] = null;
                bytesCnt = bytesCnt + 1;
                if (count >= maxElements){
                    break;
                }
            }
            curTakeIndex = this.takeIndex;
            curMaxTakeIndex = this.maxTakeIndex;
        }
        if (bytesCnt > 0) {
            unsafe.getAndAddInt(this, Offsets.memoryBytesOffset, -bytesCnt);
        }
        measureIndicator();
        return count;
    }

    

    
    public void adjustBuffer(long bufferSize) {
        // Just update buffer size limit
        this.bufferSize = bufferSize;
    }

    /**
     *  Accumulate memory bytes
     * @param byteSize byte Size
     */
    private void accumulateMemory(int byteSize){
        // Add memory count
        unsafe.getAndAddInt(this, Offsets.memoryBytesOffset, byteSize);
        while(memoryBytes >= this.bufferSize){
            // Optimize the park strategy
            LockSupport.parkNanos(1L);
        }
    }
    /**
     * Convert the long sequence to index
     * @param sequence sequenceId
     * @return position
     */
    private int toIndex(long sequence){
        return (int) (sequence & (items.length - 1));
    }

    /**
     * Measure method
     */
    private void measureIndicator(){
//        long clock = System.currentTimeMillis();
//        long measureTime = this.measureFlag;
//        if (clock >= measureTime){
//            // Only use the wait take time to measure pressure
//            long waitTime = this.waitTake;
//            if (unsafe.compareAndSwapLong(this, Offsets.measureFlagOffset,
//                    measureTime, clock + this.measureInterval)){
//                // decrease the wait take time
//                indicator.setBufferUsed(memoryBytes);
//                indicator.setPressure((double)waitTime/ ((double)(clock - measureTime) * Math.pow(10, 6)));
//                long time = unsafe.getAndAddLong(this, Offsets.waitTakeOffset, -waitTime);
//                if (time < waitTime){
//                    // Occur some error? init to zero
//                    this.waitTake = 0;
//                }
//                this.waitPut = 0;
//                //Invoke the listener
//                try {
//                    listeners.forEach(listener -> listener.onMeasure(indicator));
//                }catch(Exception e){
//                    LOG.warn("Error occurred while measuring the queue indicator", e);
//                    // Not to throw exception
//                }
//            }
//        }
    }
    private static class UnsafeUtil{

        private static final Unsafe unsafe;

        static {
            final PrivilegedExceptionAction<Unsafe> action = () -> {
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                return (Unsafe) theUnsafe.get(null);
            };
            try {
                unsafe = AccessController.doPrivileged(action);
            } catch (PrivilegedActionException e) {
                // Throw error
                throw new Error(e);
            }
        }
    }

    /**
     * Queue field offsets
     */
    private static class Offsets{
        /**
         * Take index field offset
         */
        private static final long takeIndexOffset;

        /**
         * Put index field offset
         */
        private static final long putIndexOffset;

        /**
         * Max take index field offset
         */
        private static final long maxTakeIndexOffset;

        /**
         * Memory bytes field offset
         */
        private static final long memoryBytesOffset;

        /**
         * Wait put field offset
         */
        private static final long waitPutOffset;

        /**
         * Wait take field offset
         */
        private static final long waitTakeOffset;

        /**
         * Measure flag field offset
         */
        private static final long measureFlagOffset;

        static {
            Unsafe unsafe = UnsafeUtil.unsafe;
            try {
                takeIndexOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("takeIndex"));
                putIndexOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("putIndex"));
                maxTakeIndexOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("maxTakeIndex"));
                memoryBytesOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("memoryBytes"));
                waitPutOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("waitPut"));
                waitTakeOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("waitTake"));
                measureFlagOffset = unsafe.objectFieldOffset
                        (BinlogArrayLockFreeQueue.class.getDeclaredField("measureFlag"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }

    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>((int)Math.pow(2, 10));
        Executors.newSingleThreadExecutor().submit(() -> {
            int count = 0;
            while(true) {
                String value = null;
                //                    value = queue1.poll(1, TimeUnit.SECONDS);
//                queue1.drainTo(new ArrayList<>());
                queue1.take();
                //                if (Objects.nonNull(value)){
//                    count ++;
//                } else {
//                    System.out.println("blockingQueue(num)" + count);
//                    break;
//                }
            }
        });
        for (int j = 0; j < 1; j++){
            final int finalJ = j;
            new Thread(new Runnable() {
                
                public void run() {
                    long time = System.currentTimeMillis();
                    for(int i = 0; i < 6000000; i ++){
                        try {
                            long clock = System.currentTimeMillis();
                            queue1.put("hello");
                            if (System.currentTimeMillis() - clock >= 3){
//                                System.out.println("spend1: " + (System.currentTimeMillis() - clock));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("blockingQueue" + finalJ + ": " + (System.currentTimeMillis() - time));
                }
            }).start();
        }
        BinlogArrayLockFreeQueue queue = new BinlogArrayLockFreeQueue<>((int)Math.pow(2, 10));
        Executors.newSingleThreadExecutor().submit(() -> {
            int count = 0;
            while(true) {
                //                    value = queue.take(1, TimeUnit.SECONDS);
                int size = queue.drainTo(new ArrayList<>(), Integer.MAX_VALUE);

//                    if (Objects.nonNull(value)){
//                        count = count + 1;
//                    } else {
//                        System.out.println("lockFreeQueue(num)" + count);
//                        break;
//                    }
            }
        });
        for (int j = 0; j < 1; j++){
            final int finalJ = j;
            new Thread(new Runnable() {
                
                public void run() {
                    long time = System.currentTimeMillis();
                    for(int i = 0; i < 6000000; i ++){
                        long clock = System.currentTimeMillis();
                        try {
                            queue.put("hello");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (System.currentTimeMillis() - clock >= 3){
//                            System.out.println("spend2: " + i + ":" + (System.currentTimeMillis() - clock));
                        }
                    }
                    System.out.println("lockFreeQueue" + finalJ + ": " + (System.currentTimeMillis() - time));
                }
            }).start();
        }
    }

}
