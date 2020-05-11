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

package com.webank.wedatasphere.exchangis.queue.manager;

import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.job.JobConfiguration;
import com.webank.wedatasphere.exchangis.queue.CacheDbQueue;
import com.webank.wedatasphere.exchangis.queue.dao.QueueDao;
import com.webank.wedatasphere.exchangis.queue.domain.QueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author davidhua
 * 2019/1/7
 */
public class DbQueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(DbQueueManager.class);

    private Map<String, DbQueue> queueRefs = new HashMap<>();
    private List<DbQueue> queueList = new ArrayList<>();

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private ReentrantLock syncLock = new ReentrantLock();

    private QueueDao queueDao;
    private JobConfiguration.TaskQueue config;

     DbQueueManager(QueueDao queueDao, JobConfiguration.TaskQueue config){
        this.queueDao = queueDao;
        this.config = config;
    }

    /**
     * Traverse queue list and try to lock the queue (Consumer Locker)
     * @param index begin index
     * @return the queue locked
     */
    DbQueue traverseAndLockQueue(int index){
        LOG.info("Traversal queue => total [{}], index [{}], try to get the Consumer-Lock of queue",
                queueList.size(), index);
        int preIndex = index;
        while (true) {
            DbQueue dbQueue;
            rwLock.readLock().lock();
            try{
                if(queueList.isEmpty()){
                    return null;
                }
                int size = queueList.size();
                if(size >= preIndex){
                    // if you find that the size of queue-list has changed, update 'preIndex'
                    preIndex = 0;
                }
                index = (++index) % size;
                dbQueue = queueList.get(index);
            }finally{
                rwLock.readLock().unlock();
            }
            QueueInfo info = dbQueue.queue.getInfo();
            //try to lock
            Date lockTime = Calendar.getInstance().getTime();
            if (queueDao.lock(info.getId(), AppUtil.getIpAndPort(),
                    lockTime, this.config.getQueueLockExpire()) > 0) {
                LOG.info("Succeed in getting the Consumer-Lock of queue whose id  [{}], name [{}]",
                        info.getId(), info.getqName());
                //clear the cache
                dbQueue.queue.cleanCache();
                //update last lock time
                dbQueue.lastLockTime = lockTime;
                return dbQueue;
            }
            // if preIndex == index, means that you have traversed the list once
            if (preIndex == index) {
                info = queueDao.selectOne(info.getId());
                long waitTime = 0;
                if (null != info && info.isLock() && (waitTime = info.getLockTime().getTime() +
                        this.config.getQueueLockExpire() * 1000 - Calendar.getInstance().getTimeInMillis())> 0) {
                    syncLock.lock();
                    try {
                        //wait for notify
                        LOG.info("Await for getting the Consumer-Lock of queue whose id [{}], name [{}], wait time in mills [{}]",
                                info.getId(), info.getqName(), waitTime);
                        dbQueue.noConsumerLock.await(waitTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        //ignore
                    }finally{
                        syncLock.unlock();
                    }
                }
            }
        }
    }

    /**
     * choose queue randomly, this method will support scheduler-algorithm later
     * @return
     */
    DbQueue chooseQueue(){
        rwLock.readLock().lock();
        try{
            int size = queueList.size();
            Random random = new SecureRandom();
            int choose = random.nextInt(size);
            DbQueue dbQueue = queueList.get(choose);
            return dbQueue;
        }finally{
            rwLock.readLock().unlock();
        }
    }

    DbQueue getDbQueue(String qId) {
         rwLock.readLock().lock();
         try{
             return queueRefs.get(qId);
         }finally{
             rwLock.readLock().unlock();
         }
    }

    DbQueue removeDbQueue(String qId){
         rwLock.writeLock().lock();
         try {
             LOG.info("Remove the queue id [{}] in list", qId);
             DbQueue dbQueue = queueRefs.remove(qId);
             queueList.remove(dbQueue);
             //update index
             int index = dbQueue.index;
             for(int i = index + 1; i < queueList.size(); i++){
                 queueList.get(i).index --;
             }
             return dbQueue;
         }finally{
             rwLock.writeLock().unlock();
         }
    }

    Set<String> getDbQueueIdSet(){
        rwLock.readLock().lock();
        try{
            return new HashSet<>(queueRefs.keySet());
        }finally{
            rwLock.readLock().unlock();
        }
    }

    void addDbQueue(String qId, CacheDbQueue queue){
        rwLock.writeLock().lock();
        try {
            LOG.info("Add the queue id [{}] to list", qId);
            DbQueue dbQueue = new DbQueue(queue);
            queueRefs.put(qId, dbQueue);
            queueList.add(dbQueue);
            dbQueue.index = queueList.size() - 1;
        }finally{
            rwLock.writeLock().unlock();
        }
    }

    /**
     * unlock notify (Consumer Locker)
     * @param qId
     */
    void unlockNotify(String qId){
        syncLock.lock();
        try{
            DbQueue dbQueue = getDbQueue(qId);
            if(null != dbQueue){
                //notify the other threads
                LOG.trace("Notify all threads with that queue id [{}] has released the Consumer-Lock", qId);
                dbQueue.noConsumerLock.signalAll();
            }
        }finally{
            syncLock.unlock();
        }
    }

    void unlockQueueAndNotify(String qId){
        LOG.info("Unlock queue id [{}]", qId);
        queueDao.unlock(Integer.valueOf(qId), AppUtil.getIpAndPort());
        unlockNotify(qId);
    }
    class DbQueue{
         CacheDbQueue queue;
         Condition noConsumerLock = syncLock.newCondition();
         int index = -1;
         Date lastLockTime;

         DbQueue(CacheDbQueue queue){
             this.queue = queue;
         }

    }
}
