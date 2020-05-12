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

package com.webank.wedatasphere.exchangis.exec.service;

import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeTabRelation;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUser;
import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUserBind;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecNodeUserQuery;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author davidhua
 * 2019/3/15
 */
public interface ExecNodeInfoService {

    /**
     * Check if the user has the node permission of the exec nodes(id list)
     * @param appUser
     * @param execNodeIds
     * @return
     */
    boolean haveNodeIdsPermission(String appUser, List<Integer> execNodeIds);

    Pair<List<ExecutorNode>, Boolean> haveNodeNamesPermission(String appUser, List<String> execNodeNames);
    /**
     * Get exec nodes by username and  exec nodes name list
     * @param appUser
     * @param execNodeNames
     * @return
     */
    List<ExecutorNode> getExecNodeByUserAndNames(String appUser, List<String> execNodeNames);

    /**
     * Get exec nodes
     * @param appUser
     * @return
     */
    List<ExecutorNode> getExecNodesByAppUser(String appUser);

    /**
     * Get exec nodes by app user and tab name
     * @param appUser app user
     * @param tabName tab name
     * @return
     */
    List<ExecutorNode> getExecNodesByAppUserAndTab(String appUser, String tabName);
    /**
     * @param nodeId
     * @param execUser
     * @return
     */
    ExecNodeUser getExecNodeUser(Integer nodeId, String execUser);
    /**
     * Bind executive user
     * @param bind
     * @return
     */
    int bindExecNodeAndUsers(ExecNodeUserBind bind);

    /**
     *
     * @param nodeIds
     * @param nodeNames
     * @param execUser
     * @return
     */
    int bindAndRelateExecNodesAndUser(List<Integer> nodeIds, List<String> nodeNames, ExecUser execUser);

    /**
     * Bind executive user
     * @param nodeId
     * @param execUsers
     */
    void bindExecNodeAndUsers(Integer nodeId, List<ExecUser> execUsers);

    /**
     * Unbind executive user
     * @param execNodeUser
     */
    void unBindExecNodeAndUser(ExecNodeUser execNodeUser);
    /**
     * Relate executive user
     * @param address
     * @param execNodeUser
     * @return bool
     */
    boolean relateExecNodeAndUser(String address, ExecNodeUser execNodeUser);

    /**
     * Cancel relation
     * @param execNodeUser
     * @return
     */
    boolean notRelateExecNodeAndUser(ExecNodeUser execNodeUser);

    /**
     * Find pager for 'ExecNodeUser'
     * @param query
     * @return
     */
    PageList<ExecNodeUser> findExecNodeUserPage(ExecNodeUserQuery query);

    /**
     * Select execution node
     * @param nodeId
     * @return
     */
    ExecutorNode selectExecNode(Integer nodeId);

    /**
     * Attach tab
     * @param relation
     */
    void attachTab(ExecNodeTabRelation relation);

    /**
     * @param execNodes
     * @param execUser
     * @return
     */
    List<ExecNodeUser> getExecNodeUserList(List<Integer> execNodes, String execUser);

    /**
     * Delete node
     * @param id
     */
    void deleteNode(Long id);

    /**
     * Change default
     * @param nodeId
     */
    boolean changeDefault(Integer nodeId, boolean value);

    /**
     * Get all default nodes
     * @return
     */
    List<ExecutorNode> getDefaultNodeListByTab(String tabName);
}
