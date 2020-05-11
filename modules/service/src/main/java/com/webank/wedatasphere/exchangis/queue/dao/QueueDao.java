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

package com.webank.wedatasphere.exchangis.queue.dao;

import com.webank.wedatasphere.exchangis.queue.domain.QueueInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author davidhua
 * 2019/3/11
 */
public interface QueueDao {
    /**
     * List all queues' information
     * @return
     */
    List<QueueInfo> listAll();

    /**
     * Try to lock one queue
     * @param id
     * @param lockHost
     * @param lockTime
     * @param expire expire time in seconds
     * @return
     */
    int lock(@Param("id")Integer id, @Param("lockHost")String lockHost,@Param("lockTime") Date lockTime,
             @Param("expire")long expire);

    /**
     * Unlock one queue
     * @param id
     * @param lockHost
     * @return
     */
    int unlock(@Param("id") Integer id, @Param("lockHost") String lockHost);
    /**
     * Get one queue's info
     * @param id
     * @return
     */
    QueueInfo selectOne(Integer id);
}
