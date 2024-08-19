package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.domain.RateLimit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/8/15
 */
public interface DataSourceModelMapper {

    /**
     * Select and lock
     * @param key
     * @return
     */
    DataSourceModel selectOneAndLock(Object key);

    /**
     * Select one by datasource id
     * @param dataSourceId
     * @return
     */
    DataSourceModel selectOneByDataSourceId(@Param("dataSourceId") Long dataSourceId);

    List<DataSourceModel> selectInfoList(Object key);

    /**
     * Select by model name
     * @param name model name
     * @return
     */
    DataSourceModel selectOneByName(String name);

    /**
     * Query with ratelimit
     */
    List<DataSourceModel> queryWithRateLimit();


    /**
     * Insert
     *
     * @param dataSourceModel dataSourceModelEntity
     * @return primary key
     */
    int insert(DataSourceModel dataSourceModel);

    /**
     * Delete
     *
     * @return affect rows
     */
    int delete(@Param("ids") List<Object> ids);

    /**
     * Update
     *
     * @param dataSourceModel dataSourceModelEntity
     * @return affect rows
     */
    int update(DataSourceModel dataSourceModel);

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
    DataSourceModel selectOne(Object key);

    /**
     * Search
     *
     * @return
     */
    List<DataSourceModel> findPage(PageQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<DataSourceModel> selectAllList(PageQuery pageQuery);

}
