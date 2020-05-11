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

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Created by devendeng on 2018/12/20.
 */
public interface ElementDao extends IBaseDao<QueueElement> {
    /**
     * Select the queue's polled elements  that are waiting for the completion of task
     * @param waitTimeInSec
     * @return
     */
    List<QueueElement> selectWaitForComplete(@Param("threshold")long waitTimeInSec);

}
