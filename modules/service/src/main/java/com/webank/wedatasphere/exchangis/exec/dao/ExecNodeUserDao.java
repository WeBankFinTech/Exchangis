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

import com.webank.wedatasphere.exchangis.exec.domain.ExecNodeUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecNodeUserQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author davidhua
 * 2019/10/29
 */
public interface ExecNodeUserDao {
    /**
     * Insert
     * @param execNodeUser
     */
    void insertOne(ExecNodeUser execNodeUser);

    /**
     * Select one
     * @param nodeId
     * @param execUser
     * @return
     */
    ExecNodeUser selectOne(@Param("nodeId")Integer nodeId, @Param("execUser") String execUser);
    /**
     * Update
     * @param execNodeUser
     */
    void updateOne(ExecNodeUser execNodeUser);

    /**
     * Insert
     * @param execNodeUsers
     */
    void insertBatch(List<ExecNodeUser> execNodeUsers);

    /**
     * Fetch max uid
     * @param execNodeId executor node id
     * @param userType user type
     * @return max uid
     */
    Long getMaxUid(@Param("execNodeId") int execNodeId, @Param("gid")Integer gid, @Param("userType")String userType);

    /**
     * List by executor node id
     * @param execNodeId executor node id
     * @return
     */
    List<ExecNodeUser> listByExecNodeId(int execNodeId);

    /**
     * List by executor node ids and executive user
     * @param execNodeIds
     * @param execUser
     * @return
     */
    List<ExecNodeUser> listByExecNodeIdsAndUser(@Param("execNodeIds")List<Integer> execNodeIds, @Param("execUser")String execUser);
    /**
     * Select one that marked delete
     * @param execNodeId executor node id
     * @param userType user type
     * @return
     */
    ExecNodeUser selectDelOne(@Param("execNodeId")int execNodeId, @Param("userType")String userType);
    /**
     * Mark delete
     * @param execNodeId executor node id
     * @param execUser executor user
     */
    int markDelete(@Param("execNodeId")int execNodeId, @Param("execUser") String execUser);

    /**
     * Delete
     * @param execNodeId
     * @param execUser
     * @return
     */
    int delete(@Param("execNodeId") int execNodeId, @Param("execUser") String execUser);

    /**
     * Count
     * @param query
     * @return
     */
    long count(ExecNodeUserQuery query);

    /**
     * Find page
     * @param query
     * @return
     */
    List<ExecNodeUser> findPage(ExecNodeUserQuery query, RowBounds rowBound);

    /**
     * Exist executive user
     * @param execUser
     * @return
     */
    Integer existExecUser(String execUser);

    /**
     * Delete by id of executor node
     * @param id
     */
    void deleteByNodeId(Integer id);
}
