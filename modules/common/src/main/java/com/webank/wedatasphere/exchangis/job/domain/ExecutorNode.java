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


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Executor node
 * @author davidhua
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecutorNode {
    private Integer id;
    @NotBlank(message = "{udes.domain.executor.address.notBlank}")
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date heartbeatTime;
    private Integer status;
    private Float memRate;
    private Float cpuRate;

    private List<TaskState> taskStates = new ArrayList<>();

    private List<Integer> tabIds = new ArrayList<>();

    private List<String> tabNames = new ArrayList<>();

    private boolean defaultNode = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Date heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Float getMemRate() {
        return memRate;
    }

    public void setMemRate(Float memRate) {
        this.memRate = memRate;
    }

    public Float getCpuRate() {
        return cpuRate;
    }

    public void setCpuRate(Float cpuRate) {
        this.cpuRate = cpuRate;
    }

    public List<TaskState> getTaskStates() {
        return taskStates;
    }

    public void setTaskStates(List<TaskState> taskStates) {
        this.taskStates = taskStates;
    }

    public List<Integer> getTabIds() {
        return tabIds;
    }

    public void setTabIds(List<Integer> tabIds) {
        this.tabIds = tabIds;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ExecutorNode){
            return address.equals(((ExecutorNode) obj).address);
        }
        return address.equals(obj);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    public List<String> getTabNames() {
        return tabNames;
    }

    public void setTabNames(List<String> tabNames) {
        this.tabNames = tabNames;
    }

    public boolean isDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(boolean defaultNode) {
        this.defaultNode = defaultNode;
    }
}
