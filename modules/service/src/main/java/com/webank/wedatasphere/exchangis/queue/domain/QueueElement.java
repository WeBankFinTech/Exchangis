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

package com.webank.wedatasphere.exchangis.queue.domain;

import java.util.Date;

/**
 * @author Created by devendeng on 2018/12/20.
 */
public class QueueElement {

    private Long id;

    private Integer qid;

    private Date enqTime;

    private Date pollTime;

    private Date createTime;

    private Integer enqCount;

    private Integer version;

    private Date delayTime;

    private Integer delayCount;
    /**
     * 0:int
     * 1:runing
     */
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getQid() {
        return qid;
    }

    public void setQid(Integer qid) {
        this.qid = qid;
    }

    public Date getEnqTime() {
        return enqTime;
    }

    public void setEnqTime(Date enqTime) {
        this.enqTime = enqTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getPollTime() {
        return pollTime;
    }

    public void setPollTime(Date pollTime) {
        this.pollTime = pollTime;
    }

    public Integer getEnqCount() {
        return enqCount;
    }

    public void setEnqCount(Integer enqCount) {
        this.enqCount = enqCount;
    }

    public Date getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Date delayTime) {
        this.delayTime = delayTime;
    }

    public Integer getDelayCount() {
        return delayCount;
    }

    public void setDelayCount(Integer delayCount) {
        this.delayCount = delayCount;
    }
}
