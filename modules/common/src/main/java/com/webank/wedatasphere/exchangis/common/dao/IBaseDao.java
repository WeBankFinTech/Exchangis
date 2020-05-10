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

package com.webank.wedatasphere.exchangis.common.dao;


import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by devendeng on 2018/8/20.
 */
public interface IBaseDao<T> {
    /**
     * Insert
     *
     * @param t data
     * @return primary key
     */
    int insert(T t);

    /**
     * Delete
     *
     * @return affect rows
     */
    int delete(@Param("ids") List<Object> ids);

    /**
     * Update
     *
     * @param t data
     * @return affect rows
     */
    int update(T t);

    /**
     * Count result
     *
     * @param pageQuery page query
     * @return value
     */
    long count(PageQuery pageQuery);

    /**
     * Select
     *
     * @param key primary key
     * @return data
     */
    T selectOne(Object key);

    /**
     * Search
     *
     * @return
     */
    List<T> findPage(PageQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<T> selectAllList(PageQuery pageQuery);
}
