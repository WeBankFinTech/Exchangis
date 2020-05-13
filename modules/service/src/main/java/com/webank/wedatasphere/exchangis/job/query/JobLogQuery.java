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

package com.webank.wedatasphere.exchangis.job.query;

import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by devendeng on 2018/8/24.
 */
public class JobLogQuery extends PageQuery {
    private Integer id;
    private Integer jobId;
    private Long taskId;
    private String createUser;
    private long triggerTimeBegin;
    private long triggerTimeEnd;
    private String status;
    private String jobName;
    private String fuzzyName;
    private Set<String> userDataAuth = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Date getTriggerTimeBegin() {
        return triggerTimeBegin==0 ? null:new Date(triggerTimeBegin);
    }

    public void setTriggerTimeBegin(Long triggerTimeBegin) {
        this.triggerTimeBegin = triggerTimeBegin;
    }

    public Date getTriggerTimeEnd() {
        return triggerTimeEnd==0?null:new Date(triggerTimeEnd);
    }

    public void setTriggerTimeEnd(Long triggerTimeEnd) {
        this.triggerTimeEnd = triggerTimeEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Set<String> getUserDataAuth() {
        return userDataAuth;
    }

    public void setUserDataAuth(Set<String> userDataAuth) {
        this.userDataAuth = userDataAuth;
    }

    public String getFuzzyName() {
        return fuzzyName;
    }

    public void setFuzzyName(String fuzzyName) {
        this.fuzzyName = fuzzyName;
    }
}


