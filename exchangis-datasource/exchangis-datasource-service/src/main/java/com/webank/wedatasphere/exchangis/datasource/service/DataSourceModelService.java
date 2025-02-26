package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelation;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.exception.DataSourceModelOperateException;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisOnEventException;

import java.util.List;

/**
 * Data source model service
 */
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
    boolean delete(Long id) throws DataSourceModelOperateException, RateLimitOperationException;

    /**
     * Update once
     *
     * @param model data source model entity
     * @return boolean
     */
    boolean update(DataSourceModel model) throws DataSourceModelOperateException;

    /**
     * Update dataSourceModel and related dataSource
     *
     * @param model data source model entity
     * @return boolean
     */
    void updateRelated(DataSourceModel model) throws DataSourceModelOperateException, ExchangisOnEventException, ExchangisDataSourceException;

    /**
     * Begin update transaction
     * @param modelId model id
     * @return duplicate model
     */
    DataSourceModel beginUpdate(Long modelId, DataSourceModel update) throws DataSourceModelOperateException;

    /**
     * Commit update transaction
     * @param modelId model id
     * @param commit duplicated model
     */
    void commitUpdate(Long modelId, DataSourceModel commit, DataSourceModel update) throws DataSourceModelOperateException;

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
    PageResult<DataSourceModel> findPage(PageQuery pageQuery);

    /**
     * Select all
     *
     * @return
     */
    List<DataSourceModel> selectAllList(DataSourceModelQuery query);

    /**
     * Query relations between model and data source
     * @param id model id
     * @return relations
     */
    List<DataSourceModelRelation> queryRelations(Long id);

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
