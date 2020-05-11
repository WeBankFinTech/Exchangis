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

package com.webank.wedatasphere.exchangis.auth.domain;

import java.util.Date;

/**
 * @author davidhua
 * 2019/4/10
 */
public class UserExecNode {

    private String appUser;

    private Integer execNodeId;

    private Date createTime;

    public UserExecNode(){

    }

    public UserExecNode(String appUser, Integer execNodeId){
        this.appUser = appUser;
        this.execNodeId = execNodeId;
    }
    public String getAppUser() {
        return appUser;
    }

    public void setAppUser(String appUser) {
        this.appUser = appUser;
    }

    public Integer getExecNodeId() {
        return execNodeId;
    }

    public void setExecNodeId(Integer execNodeId) {
        this.execNodeId = execNodeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
