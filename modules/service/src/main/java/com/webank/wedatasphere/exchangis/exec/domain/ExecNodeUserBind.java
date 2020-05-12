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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author davidhua
 * 2019/11/16
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecNodeUserBind {
    public enum BindOpType{
        /**
         * BIND
         */
        BIND,
        /**
         * BIND AND RELATE
         */
        BIND_RELATE
    }

    private BindOpType opType = BindOpType.BIND;

    /**
     * @NotBlank(message = "{udes.domain.exec.node.id.notBlank}")
     * exec node id
     */
    private Integer nodeId;
    /**
     * execUser list needed to be bound
     */
    @Valid
    private List<ExecUser> execUserList = new ArrayList<>();

    public BindOpType getOpType() {
        return opType;
    }

    public void setOpType(BindOpType opType) {
        this.opType = opType;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public List<ExecUser> getExecUserList() {
        return execUserList;
    }

    public void setExecUserList(List<ExecUser> execUserList) {
        this.execUserList = execUserList;
    }
}
