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

package com.webank.wedatasphere.exchangis.exec.dao;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by devendeng on 2018/8/24.
 * @author devendeng
 */
public interface ExecNodeDao extends IBaseDao<ExecutorNode> {

    /**
     * Update heartbeat information
     * @param node node information
     * @return
     */
    int updateHeartbeat(ExecutorNode node);

    /**
     * Select node information
     * @param address address
     * @return
     */
    ExecutorNode getByAddress(String address);

    /**
     * Get available nodes by address list and heartbeat time
     * @param seconds heartbeat range
     * @param addressList address list
     * @return
     */
    List<ExecutorNode> getAvailByAddressList(@Param("addressList")List<String> addressList, @Param("heartbeat") long seconds);

    /**
     * Get available nodes
     * @param seconds heartbeat interval
     * @return
     */
    List<ExecutorNode> getAvails(@Param("heartbeat")long seconds);

    /**
     * Select one
     * @param nodeId
     * @return
     */
    ExecutorNode selectOne(Integer nodeId);

    /**
     * Update default column
     * @param nodeId
     * @param value
     */
    int updateDefault(@Param("nodeId")Integer nodeId, @Param("defaultNode")Boolean value);

    /**
     * Get default nodes by tab name
     * @return
     */
    List<ExecutorNode> getDefaultNodesByTab(@Param("tabName")String tabName);

    /**
     * Count default node by id list
     * @param execNodeIds
     * @return
     */
    int countDefaultByIds(List<Integer> execNodeIds);

    /**
     * Count default node by name list
     * @param copyExecNodeNames
     * @return
     */
    int countDefaultByNames(List<String> copyExecNodeNames);
}
