package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageList;
import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelation;

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
    boolean delete(Long id);


    /**
     * Update once
     *
     * @param model data source model entity
     * @return boolean
     */
    boolean update(DataSourceModel model);

    /**
     * Begin update transaction
     * @param modelId model id
     * @return duplicate model
     */
    DataSourceModel beginUpdate(Long modelId, DataSourceModel update);

    /**
     * Commit update transaction
     * @param modelId model id
     * @param commit duplicated model
     */
    void commitUpdate(Long modelId, DataSourceModel commit, DataSourceModel update);

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
