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

package com.webank.wedatasphere.exchangis.auth.dao;

import com.webank.wedatasphere.exchangis.auth.domain.UserExecNode;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author davidhua
 * 2019/4/10
 */
public interface UserExecNodeDao {
    /**
     * Get exec node list by username
     * @param username
     * @return
     */
    List<ExecutorNode> getExecNodeByUser(String username);

    /**
     * Get available nodes by username
     * @param username
     * @param heartBeatAvailInterval
     * @return
     */
    List<ExecutorNode> getAvailNodesByUser(@Param("username")String username, @Param("heartbeat") long heartBeatAvailInterval);

    /**
     * get exec node list by username and id list
     * @param username
     * @param ids
     * @return
     */
    List<ExecutorNode> getExecNodeByUserAndIds(@Param("username") String username, @Param("ids") List<Integer> ids);

    /**
     * get exec node list by username and tab name
     * @param username
     * @param tabName
     * @return
     */
    List<ExecutorNode> getExecNodeByUserAndTab(@Param("username") String username, @Param("tabName")String tabName);
    /**
     * get exec node list by username and name list
     * @param username
     * @param names
     * @return
     */
    List<ExecutorNode> getExecNodeByUserAndNames(@Param("username") String username, @Param("names") List<String> names);


    /**
     * count
     * @param userId
     * @return
     */
    long count(Integer userId);

    /**
     * find page
     * @param userId
     * @return
     */
    List<ExecutorNode> findPageByUserId(@Param("userId")Integer userId, @Param("page")PageQuery query, RowBounds rowBound);

    /**
     * add one
     * @param userExecNode
     */
    void addOne(UserExecNode userExecNode);

    /**
     * delete one
     * @param userExecNode
     */
    void deleteOne(UserExecNode userExecNode);

    /**
     * exist app user
     * @param appUser
     * @return
     */
    Integer existsAppUser(String appUser);

    /**
     * Delete by id of executor node
     * @param id
     */
    int deleteByNodeId(Integer id);
}
