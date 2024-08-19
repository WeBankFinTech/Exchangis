package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/8/15
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
    List<DataSourceModel> findPage(PageQuery pageQuery);

    /**
     * Select all
     *
     * @return
     */
    List<DataSourceModel> selectAllList(PageQuery pageQuery);

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
