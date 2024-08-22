package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;

import java.util.List;

public interface DataSourceModelService {

    /**
     * Add
     *
     * @param dataSourceModel
     * @return
     */
    boolean add(DataSourceModel dataSourceModel);

    /**s
     * Delete batch(collection)
     *
     * @return
     */
    boolean delete(List<Object> ids);

    /**
     * Delete one
     *
     * @param id
     */
    boolean delete(Long id);

    /**
     * Update
     *
     * @param dataSourceModel
     * @return
     */
    boolean update(DataSourceModel dataSourceModel);

    /**
     * Count
     *
     * @param pageQuery
     * @return
     */
    long getCount(PageQuery pageQuery);

    /**
     * Find
     *
     * @return
     */
    PageList<DataSourceModel> findPage(PageQuery pageQuery);

    /**
     * Select all
     *
     * @return
     */
    List<DataSourceModel> selectAllList(DataSourceModelQuery query);

    DataSourceModel get(Long id);

    /**
     * Exist
     * @param id
     * @return
     */
    boolean exist(Long id);

    /**
     * Query with ratelimit
     */
    List<DataSourceModel> queryWithRateLimit();
}
