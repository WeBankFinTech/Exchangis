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

package com.webank.wedatasphere.exchangis.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author enjoyyin
 * 2019/2/14
 */
public class JobConfiguration {
    @Component
    @PropertySource(value="classpath:task-run.properties", ignoreResourceNotFound = true)
    public static class TaskRun{
        @Value("${task.run.history.batch-size:1000}")
        private int historyBatchSize;

        public int getHistoryBatchSize() {
            return historyBatchSize;
        }

    }
    @Component
    @PropertySource(value= "classpath:task-alive.properties", ignoreResourceNotFound =  true)
    public static class TaskAlive{
        @Value("${task.alive.threadPool.threadNum:10}")
        private int alivePoolNum;

        @Value("${task.alive.threadPool.threadPriority:5}")
        private int alivePoolCore;

        @Value("${task.alive.threadPool.queue:500}")
        private int alivePoolQueueSize;

        @Value("${task.alive.sched.interval:60}")
        private int aliveSchedInterval;

        @Value("${task.alive.sched.checkNum:500}")
        private int aliveSchedCheckNum;

        @Value("${task.alive.sched.id:ALIVE-CHECK-0}")
        private String aliveSchedId;

        public int getAlivePoolNum() {
            return alivePoolNum;
        }

        public int getAlivePoolCore() {
            return alivePoolCore;
        }

        public int getAliveSchedCheckNum() {
            return aliveSchedCheckNum;
        }

        public String getAliveSchedId() {
            return aliveSchedId;
        }

        public int getAliveSchedInterval() {
            return aliveSchedInterval;
        }

        public int getAlivePoolQueueSize() {
            return alivePoolQueueSize;
        }
    }

    @Component
    @PropertySource(value="classpath:task-queue.properties", ignoreResourceNotFound = true)
    public static class TaskQueue{
        @Value("${task.queue.sync.timeInSec}")
        private int queueSyncTime;

        @Value("${task.queue.lock.expireInSec}")
        private int queueLockExpire;

        @Value("${task.queue.cache.size}")
        private int queueCacheSize;

        @Value("${task.queue.idle.timeInSec}")
        private int queueIdleTime;

        @Value("${task.queue.poll.delayInSec}")
        private int[] queuePollDelayTime;

        @Value("${task.queue.max.enq}")
        private int queueMaxEnq;

        @Value("${task.queue.consumer.number:1}")
        private int queueConsumerNum;

        public int getQueueSyncTime() {
            return queueSyncTime;
        }

        public int getQueueLockExpire() {
            return queueLockExpire;
        }

        public int getQueueCacheSize() {
            return queueCacheSize;
        }

        public int getQueueIdleTime() {
            return queueIdleTime;
        }

        public int getQueueMaxEnq() {
            return queueMaxEnq;
        }

        public int[] getQueuePollDelayTime(){
            return queuePollDelayTime;
        }

        public int getQueueConsumerNum() {
            return queueConsumerNum;
        }
    }

    @Component
    @PropertySource(value="classpath:task-queue-repair.properties", ignoreResourceNotFound = true)
    public static class TaskQueueRepair{

        @Value("${task.queue.repair.sched.id}")
        private String queueRepairSchedId;

        @Value("${task.queue.repair.interval-in-seconds}")
        private int queueRepairInterval;

        @Value("${task.queue.repair.threshold-in-seconds}")
        private int queueRepairThreshold;

        public String getQueueRepairSchedId() {
            return queueRepairSchedId;
        }

        public void setQueueRepairSchedId(String queueRepairSchedId) {
            this.queueRepairSchedId = queueRepairSchedId;
        }

        public int getQueueRepairInterval() {
            return queueRepairInterval;
        }

        public void setQueueRepairInterval(int queueRepairInterval) {
            this.queueRepairInterval = queueRepairInterval;
        }

        public int getQueueRepairThreshold() {
            return queueRepairThreshold;
        }

        public void setQueueRepairThreshold(int queueRepairThreshold) {
            this.queueRepairThreshold = queueRepairThreshold;
        }
    }
}
