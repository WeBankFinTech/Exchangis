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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * @author davidhua
 * 2019/12/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecNodeTab {

    private Integer nodeId;

    private Integer tabId;

    private String tabName;

    private Date createTime;

    public ExecNodeTab(){

    }

    public ExecNodeTab(Integer nodeId, Integer tabId, String tabName){
        this.nodeId = nodeId;
        this.tabId = tabId;
        this.tabName = tabName;
    }
    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getTabId() {
        return tabId;
    }

    public void setTabId(Integer tabId) {
        this.tabId = tabId;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
