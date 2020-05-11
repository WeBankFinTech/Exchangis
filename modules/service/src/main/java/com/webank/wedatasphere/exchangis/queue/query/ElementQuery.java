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

package com.webank.wedatasphere.exchangis.queue.query;

import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;

/**
 * @author Created by devendeng on 2018/12/21.
 */
public class ElementQuery extends PageQuery {
    private Integer qid;

    private Integer status;

    private Integer useDelay;

    public Integer getQid() {
        return qid;
    }

    public void setQid(Integer qid) {
        this.qid = qid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUseDelay() {
        return useDelay;
    }

    public void setUseDelay(Integer useDelay) {
        this.useDelay = useDelay;
    }
}
