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

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.common.util.page.Paginator;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author devendeng on 2018/8/20.
 */

public abstract class AbstractGenericService<T> implements IBaseService<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGenericService.class);
    protected abstract IBaseDao<T> getDao();

    /**
     * Add
     *
     */
    @Override
    public boolean add(T t) throws Exception {
        return getDao().insert(t) > 0;
    }

    /**
     * Delete
     */
    @Override
    public boolean delete(List<Object> ids) {
        return getDao().delete(ids) > 0;
    }

    /**
     * Delete
     */
    @Override
    public boolean delete(String ids) {
        String[] idsStr = ids.split(",");
        List<Object> list = new ArrayList<Object>();
        for (String id : idsStr) {
            list.add(Long.valueOf(id));
        }
        logger.info("Delete ids: " + ids);
        return delete(list);
    }

    /**
     * Update
     *
     */
    @Override
    public boolean update(T t) {
        return getDao().update(t) > 0;
    }

    /**
     * (non-Javadoc)
     *
     */
    @Override
    @Transactional(readOnly = true)
    public long getCount(PageQuery pageQuery) {
        return getDao().count(pageQuery);
    }

    /**
     * Get
     *
     */
    @Override
    @Transactional(readOnly = true)
    public T get(Object id) {
        return queryFilter(getDao().selectOne(id));
    }

    /**
     * Find page
     */
    @Override
    @Transactional(readOnly = true)
    public PageList<T> findPage(PageQuery pageQuery) {
        long totalCount = getDao().count(pageQuery);
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        PageList<T> page = new PageList<T>(new Paginator(currentPage, pageSize, (int) totalCount));
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<T> result = getDao().findPage(pageQuery, new RowBounds(offset, pageSize));
        page.setData(queryFilter(result));
        return page;
    }

    /**
     * Select all
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> selectAllList(PageQuery pageQuery) {
        return queryFilter(getDao().selectAllList(pageQuery));
    }

    /**
     * Filter of query
     * @param t data
     * @return
     */
    protected T queryFilter(T t){
        return t;
    }

    final protected List<T> queryFilter(List<T> list){
        List<T> result = new ArrayList<>();
        for(T t : list){
            result.add(queryFilter(t));
        }
        return result;
    }


}
