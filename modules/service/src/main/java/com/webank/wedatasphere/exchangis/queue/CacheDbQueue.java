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

package com.webank.wedatasphere.exchangis.queue;

import com.webank.wedatasphere.exchangis.queue.dao.ElementDao;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;
import com.webank.wedatasphere.exchangis.queue.domain.QueueInfo;
import com.webank.wedatasphere.exchangis.queue.query.ElementQuery;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author davidhua
 * 2019/1/7
 */
public class CacheDbQueue extends AbstractAdapterQueue{
    private static final int DEFAULT_CACHE_SIZE = 10;

    private ReadWriteLock rw = new ReentrantReadWriteLock();

    private QueueInfo info;

    private ElementDao dao;

    private List<QueueElement> cache = new ArrayList<>();

    private int cacheSize = 0;

    private AtomicInteger pos = new AtomicInteger(0);

    public CacheDbQueue(QueueInfo queueInfo, ElementDao dao, int cacheSize){
        this.info = queueInfo;
        this.dao = dao;
        this.cacheSize = cacheSize;
    }

    public CacheDbQueue(QueueInfo queueInfo, ElementDao dao){
        this(queueInfo, dao, DEFAULT_CACHE_SIZE);
    }
    @Override
    public boolean add(QueueElement queueElement) {
        queueElement.setPollTime(null);
        if(queueElement.getQid() != null &&
                queueElement.getStatus() == 0){
            queueElement.setQid(info.getId());
            //Means to update queue element
            return dao.update(queueElement) > 0;
        }
        queueElement.setQid(info.getId());
        //Means to enqueue
        return dao.insert(queueElement) > 0;
    }

    @Override
    public boolean remove(Object o) {
        //Remove actually
        if(o instanceof QueueElement){
            QueueElement element = (QueueElement)o;
            return dao.delete(Collections.singletonList(element.getId())) > 0;
        }
        return false;
    }

    @Override
    public void clear() {
        //Ignore
    }

    public void cleanCache(){
        rw.writeLock().lock();
        try {
            cache.clear();
            pos.set(0);
        }finally{
            rw.writeLock().unlock();
        }
    }

    @Override
    public boolean offer(QueueElement queueElement) {
        return add(queueElement);
    }

    @Override
    public QueueElement remove() {
        return pollFromCache();
    }

    @Override
    public QueueElement poll() {
        return pollFromCache();
    }

    @Override
    public QueueElement element() {
        return peekFromCache();
    }

    @Override
    public QueueElement peek() {
        return peekFromCache();
    }

    public QueueInfo getInfo(){
        return info;
    }
    private QueueElement pollFromCache(){
        rw.writeLock().lock();
        try {
            if (this.pos.get() == this.cache.size()) {
                flushCache();
                if (cache.isEmpty()) {
                    return null;
                }
            }
            //Delete mark, actually change the element's status
            QueueElement element = cache.get(this.pos.get());
            if (element != null) {
                element.setStatus(1);
                element.setPollTime(Calendar.getInstance().getTime());
                if(dao.update(element) <= 0){
                    //have been poll
                    element = new QueueElement();
                    element.setId(null);
                }else{
                    element.setVersion(element.getVersion() + 1);
                }
            }
            this.pos.incrementAndGet();
            return element;
        }finally{
            rw.writeLock().unlock();
        }
    }

    private QueueElement peekFromCache(){
        rw.readLock().lock();
        try {
            if (this.pos.get() == this.cache.size()) {
                flushCache();
                if (cache.isEmpty()) {
                    return null;
                }
            }
            return cache.get(this.pos.get());
        }finally{
            rw.readLock().unlock();
        }
    }

    private synchronized void flushCache(){
        if(this.pos.get() == this.cache.size()) {
            ElementQuery query = new ElementQuery();
            query.setQid(info.getId());
            query.setStatus(0);
            query.setUseDelay(1);
            List<QueueElement> list = dao.findPage(query, new RowBounds(0, cacheSize));
            if (!list.isEmpty()) {
                cache = list;
            } else {
                cache = new ArrayList<>();
            }
            pos.set(0);
        }
    }
}
