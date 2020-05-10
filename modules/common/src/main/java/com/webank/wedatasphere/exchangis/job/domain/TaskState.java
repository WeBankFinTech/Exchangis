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

package com.webank.wedatasphere.exchangis.job.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author davidhua
 * 2019/11/12
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskState {

    @NotNull
    private Long taskId;

    private Long currentByteSpeed;

    private Date versionTime;

    public TaskState(TaskState other){
        this.taskId = other.taskId;
        this.currentByteSpeed = other.currentByteSpeed;
        this.versionTime = other.versionTime;
    }

    public TaskState(){

    }

    public TaskState(Long taskId){
        this.taskId = taskId;
    }
    public Long getTaskId() {
        return taskId;
    }

    public Long getCurrentByteSpeed() {
        return currentByteSpeed;
    }

    public void setCurrentByteSpeed(Long currentByteSpeed) {
        this.currentByteSpeed = currentByteSpeed;
    }


    public Date getVersionTime() {
        return versionTime;
    }

    public void setVersionTime(Date versionTime) {
        this.versionTime = versionTime;
    }
}
