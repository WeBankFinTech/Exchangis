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

package com.webank.wedatasphere.exchangis.executor;


import com.webank.wedatasphere.exchangis.job.domain.TaskState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author davidhua
 * 2019/11/12
 */
@Component
public class JobContainerManager {
    private ConcurrentHashMap<String, JobContainer> jobContainers = new ConcurrentHashMap<>();

    public JobContainerManager(){
    }

    public JobContainer removeContainer(Object containerId){
        return this.jobContainers.remove(String.valueOf(containerId));
    }

    public boolean addContainer(Object containerId, JobContainer jobContainer){
        return this.jobContainers.putIfAbsent(String.valueOf(containerId), jobContainer) == null;
    }

    public JobContainer getContainer(Object containerId){
        return this.jobContainers.get(String.valueOf(containerId));
    }

    /**
     * Collect all the task states
     * @return
     */
    public List<TaskState> collectStateList(){
        List<TaskState> stateList = new ArrayList<>();
        jobContainers.forEach((key, value) -> stateList.add(value.getState()));
        return stateList;
    }

    public boolean updateTaskState(Object containerId, TaskState state){
        JobContainer container = getContainer(containerId);
        if(null != container){
            container.updateTaskState(state);
            return true;
        }
        return false;
    }

}
