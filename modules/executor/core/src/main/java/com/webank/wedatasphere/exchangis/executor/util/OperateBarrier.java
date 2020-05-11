/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.executor.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author davidhua
 * 2020/3/26
 */
public class OperateBarrier {
    /**
     * Barriers
     */
    private static ConcurrentHashMap<String, Barrier> barrierMap = new ConcurrentHashMap<>();
    /**
     * Active time of barrier
     */
    private static final int BARRIER_EXIST_IN_SECONDS = 5;
    public static <R>R operate(String operateId, Supplier<R> operate){
        Barrier barrier = barrierMap.computeIfAbsent(operateId, key -> new Barrier(operateId));
        boolean signal = inQueue(barrier);
        try{
            //Execute in async
            return operate.get();
        }finally{
            deQueue(barrier, signal);

        }
    }

    private static boolean inQueue(Barrier barrier){
        boolean signal = true;
        barrier.barrierLock.lock();
        try {
            if(barrier.enQueueCount.get() > 0){
                //Just wait in certain time
                signal = barrier.enQueueCondition.await(BARRIER_EXIST_IN_SECONDS, TimeUnit.SECONDS);
            }
            barrier.enQueueCount.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            //Ignore
        } finally{
            barrier.barrierLock.unlock();
        }
        return signal;
    }

    private static void  deQueue(Barrier barrier, boolean signal){
        barrier.barrierLock.lock();
        try{
            if(barrier.enQueueCount.get() > 0 ){
                if(signal) {
                    //Just signal one thread
                    barrier.enQueueCondition.signal();
                }
                barrier.enQueueCount.decrementAndGet();
            }
            if(!barrier.barrierLock.hasWaiters(barrier.enQueueCondition)) {
                barrierMap.remove(barrier.id);
            }
        }finally{
            barrier.barrierLock.unlock();
        }
    }
    private static class Barrier{

        private final ReentrantLock barrierLock;
        /**
         * In queue
         */
        private final Condition enQueueCondition;

        private String id;

        private final AtomicInteger enQueueCount = new AtomicInteger(0);
        Barrier(String id){
            this.id = id;
            barrierLock = new ReentrantLock();
            enQueueCondition = barrierLock.newCondition();
        }
    }
}
