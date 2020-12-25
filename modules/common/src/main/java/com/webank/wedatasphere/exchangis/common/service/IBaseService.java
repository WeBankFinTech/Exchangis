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

package com.webank.wedatasphere.exchangis.common.service;

import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;

import java.util.List;

/**
 * Created by devendeng on 2018/8/20.
 */
public interface IBaseService<T> {

    /**
     * Add
     *
     * @param t
     * @return
     */
    boolean add(T t) throws Exception;

    /**s
     * Delete batch(collection)
     *
     * @return
     */
    boolean delete(List<Object> ids);

    /**
     * Delete batch
     *
     * @param ids
     */
    boolean delete(String ids);

    /**
     * Update
     *
     * @param t
     * @return
     */
    boolean update(T t);

    /**
     * Count
     *
     * @param pageQuery
     * @return
     */
    long getCount(PageQuery pageQuery);

    /**
     * Select
     *
     * @param id
     * @return
     */
    T get(Object id);

    /**
     * Find
     *
     * @return
     */
    PageList<T> findPage(PageQuery pageQuery);

    /**
     * Select all
     *
     * @return
     */
    List<T> selectAllList(PageQuery pageQuery);
}