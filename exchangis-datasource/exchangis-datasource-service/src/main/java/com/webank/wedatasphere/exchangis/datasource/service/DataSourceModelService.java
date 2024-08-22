package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModel;

import java.util.List;

public interface DataSourceModelService {

    /**
     * Add
     *
     * @param dataSourceModel
     * @return
     */
    boolean add(ExchangisDataSourceModel dataSourceModel);

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
     * @param dataSourceModel
     * @return
     */
    boolean update(ExchangisDataSourceModel dataSourceModel);

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
    PageList<ExchangisDataSourceModel> findPage(PageQuery pageQuery);

    /**
     * Select all
     *
     * @return
     */
    List<ExchangisDataSourceModel> selectAllList(DataSourceModelQuery query);

    ExchangisDataSourceModel get(Long id);

    /**
     * Exist
     * @param id
     * @return
     */
    boolean exist(Long id);

    /**
     * Query with ratelimit
     */
    List<ExchangisDataSourceModel> queryWithRateLimit();
}
