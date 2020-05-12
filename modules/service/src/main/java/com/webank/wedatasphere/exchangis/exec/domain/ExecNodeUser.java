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

package com.webank.wedatasphere.exchangis.exec.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * User in executor node
 * @author davidhua
 * 2019/10/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecNodeUser {
    /**
     * node id
     */
    private Integer execNodeId;
    /**
     * executor user
     */
    private String execUser;
    /**
     * relation state
     */
    private int relationState;

    /**
     * user type
     */
    private String userType;
    /**
     * uid
     */
    private Integer uid;

    /**
     * gid
     */
    private Integer gid;

    /**
     * mark delete
     */
    private boolean markDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Integer getExecNodeId() {
        return execNodeId;
    }

    public void setExecNodeId(Integer execNodeId) {
        this.execNodeId = execNodeId;
    }

    public String getExecUser() {
        return execUser;
    }

    public void setExecUser(String execUser) {
        this.execUser = execUser;
    }

    public int getRelationState() {
        return relationState;
    }

    public void setRelationState(int relationState) {
        this.relationState = relationState;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public boolean isMarkDel() {
        return markDel;
    }

    public void setMarkDel(boolean markDel) {
        this.markDel = markDel;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
