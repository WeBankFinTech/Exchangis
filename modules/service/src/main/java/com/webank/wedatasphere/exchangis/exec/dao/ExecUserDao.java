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


import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecUserQuery;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author davidhua
 * 2019/3/15
 */
public interface ExecUserDao {
    /**
     * List all
     * @return
     */
    List<ExecUser> listAll();

    /**
     * Insert one
     * @param execUser
     */
    void insertOne(ExecUser execUser);

    /**
     * Select by username
     * @param execUser
     * @return
     */
    ExecUser selectByName(String execUser);

    /**
     * Select
     * @param id
     * @return
     */
    ExecUser selectOne(Integer id);

    /**
     * Delete
     * @param id
     * @return
     */
    int deleteOne(Integer id);

    /**
     * Count
     * @param query
     * @return
     */
    long count(ExecUserQuery query);

    /**
     * Find page
     * @param rowBounds
     * @return
     */
    List<ExecUser> findPage(ExecUserQuery query, RowBounds rowBounds);
}
