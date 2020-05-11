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

import com.webank.wedatasphere.exchangis.job.JobConfiguration;
import com.webank.wedatasphere.exchangis.job.service.JobTaskService;
import com.webank.wedatasphere.exchangis.queue.CacheDbQueue;
import com.webank.wedatasphere.exchangis.queue.dao.ElementDao;
import com.webank.wedatasphere.exchangis.queue.dao.QueueDao;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;
import com.webank.wedatasphere.exchangis.queue.domain.QueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author davidhua
 * 2019/1/7
 */
@Component
public class QueueSchedulerManager {
    private static final Logger LOG = LoggerFactory.getLogger(QueueSchedulerManager.class);
    private static final String QUEUE_SCHEDULE_POOL_PREFIX = "Queue-Schedule-";
    private DbQueueManager dbQueueManager;
    private ScheduledExecutorService executorService;

    @Resource
    private QueueDao queueDao;

    @Resource
    private ElementDao elementDao;

    @Resource
    private JobConfiguration.TaskQueue taskQueueConf;
    @Resource
    private JobTaskService jobTaskService;

    private volatile int traverseIndex;

    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    public void init(){
        dbQueueManager = new DbQueueManager(queueDao, taskQueueConf);
        executorService = queueScheduledExecutor(taskQueueConf.getQueueConsumerNum() + 1);
        syncQueueFromDb();
        //to sync queue list
        executorService.scheduleAtFixedRate(this::syncQueueFromDb, taskQueueConf.getQueueSyncTime(), taskQueueConf.getQueueSyncTime(), TimeUnit.SECONDS);
        for(int i = 0; i <  taskQueueConf.getQueueConsumerNum(); i++) {
            executorService.schedule(() -> {
                while (true) {
                    try {
                        DbQueueManager.DbQueue dbQueue = dbQueueManager.traverseAndLockQueue(traverseIndex);
                        //Update the index
                        if (null != dbQueue) {
                            traverseIndex = dbQueue.index;
                            CacheDbQueue queue = dbQueue.queue;
                            String qId = String.valueOf(queue.getInfo().getId());
                            long expireTime = dbQueue.lastLockTime.getTime() + taskQueueConf.getQueueLockExpire() * 1000L;
                            while (Calendar.getInstance().getTimeInMillis() < expireTime) {
                                try {
                                    QueueElement element = queue.poll();
                                    if (null == element) {
                                        //queue is empty, sleep idle time
                                        Thread.sleep(taskQueueConf.getQueueIdleTime() * 1000L);
                                        if (Calendar.getInstance().getTimeInMillis() >= expireTime ||
                                                (element = queue.poll()) == null) {
                                            //unlock queue
                                            dbQueueManager.unlockQueueAndNotify(String.valueOf(queue.getInfo().getId()));
                                            break;
                                        }
                                    }
                                    Long id = element.getId();
                                    if (id == null) {
                                        continue;
                                    }
                                    boolean result = jobTaskService.runTask(element);
                                    if (!result) {
                                        //add in queue again
                                        chooseQueue().add(element);
                                    }
                                } catch (Exception e) {
                                    LOG.error("SYSTEM_EXCEPTION: Error in consuming queue id [{}]", qId, e);
                                    //unlock queue
                                    dbQueueManager.unlockQueueAndNotify(String.valueOf(queue.getInfo().getId()));
                                    break;
                                }
                            }
                        } else {
                            //Cannot find dbQueue, so sleep to wait for the synchronization of queue list
                            Thread.sleep(taskQueueConf.getQueueSyncTime() * 1000L);
                        }
                    } catch (Exception e) {
                        LOG.error("Error in scheduling to consume queue", e);
                        try {
                            //When error happened, just sleep once
                            Thread.sleep(1000);
                        } catch (Exception e1) {
                            //ignore
                        }
                    }
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

    public Queue<QueueElement> chooseQueue(){
        return dbQueueManager.chooseQueue().queue;
    }

    private void syncQueueFromDb(){
        try {
            LOG.info("Start to sync queue info");
            List<QueueInfo> queues = queueDao.listAll();
            Set<String> qidSet = dbQueueManager.getDbQueueIdSet();
            queues.forEach(queueInfo -> {
                String qid = String.valueOf(queueInfo.getId());
                qidSet.remove(qid);
                DbQueueManager.DbQueue dbQueue = dbQueueManager.getDbQueue(qid);
                if(null != dbQueue) {
                    CacheDbQueue queue = dbQueue.queue;
                    QueueInfo info = queue.getInfo();
                    info.setCreateTime(queueInfo.getCreateTime());
                    info.setDescription(queueInfo.getDescription());
                    info.setLock(queueInfo.isLock());
                    info.setLockHost(queueInfo.getLockHost());
                    info.setLockTime(queueInfo.getLockTime());
                    info.setqName(queueInfo.getqName());
                    info.setPriority(queueInfo.getPriority());
                    if (!info.isLock()) {
                        dbQueueManager.unlockNotify(qid);
                    }
                }else{
                    dbQueueManager.addDbQueue(qid, new CacheDbQueue(queueInfo, elementDao,
                            taskQueueConf.getQueueCacheSize()));
                }
            });
            //queue deleted
            if(!qidSet.isEmpty()){
                qidSet.forEach(qid -> dbQueueManager.removeDbQueue(qid));
            }
        }catch(Exception e){
            LOG.error("Error in scheduling to sync queue info", e);
        }
    }
    private ScheduledThreadPoolExecutor queueScheduledExecutor(int coreSize){
        return new ScheduledThreadPoolExecutor(coreSize, new ThreadFactory() {
            private final ThreadGroup group = Thread.currentThread().getThreadGroup();
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r,
                        QUEUE_SCHEDULE_POOL_PREFIX + threadNumber.getAndIncrement(),
                        0);
                if(t.isDaemon()) {
                    t.setDaemon(false);
                }
                if(t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY);
                }
                return t;
            }
        });
    }
}
