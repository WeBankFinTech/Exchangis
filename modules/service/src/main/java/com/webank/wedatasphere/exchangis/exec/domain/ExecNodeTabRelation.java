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

import java.util.ArrayList;
import java.util.List;

/**
 * @author davidhua
 * 2019/12/29
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecNodeTabRelation {
    private Integer nodeId;

    private List<Integer> tabIds = new ArrayList<>();

    private List<String> tabNames = new ArrayList<>();

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public List<Integer> getTabIds() {
        return tabIds;
    }

    public void setTabIds(List<Integer> tabIds) {
        this.tabIds = tabIds;
    }

    public List<String> getTabNames() {
        return tabNames;
    }

    public void setTabNames(List<String> tabNames) {
        this.tabNames = tabNames;
    }
}
