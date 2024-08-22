package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface DataSourceModelMapper {

    /**
     * Select and lock
     * @param key
     * @return
     */
    ExchangisDataSourceModel selectOneAndLock(Object key);

    /**
     * Select one by datasource id
     * @param dataSourceId
     * @return
     */
    ExchangisDataSourceModel selectOneByDataSourceId(@Param("dataSourceId") Long dataSourceId);

    List<ExchangisDataSourceModel> selectInfoList(Object key);

    /**
     * Select by model name
     * @param name model name
     * @return
     */
    ExchangisDataSourceModel selectOneByName(String name);

    /**
     * Query with ratelimit
     */
    List<ExchangisDataSourceModel> queryWithRateLimit();


    /**
     * Insert
     *
     * @param dataSourceModel dataSourceModelEntity
     * @return primary key
     */
    int insert(ExchangisDataSourceModel dataSourceModel);

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
    int update(ExchangisDataSourceModel dataSourceModel);

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
    ExchangisDataSourceModel selectOne(Object key);

    /**
     * Search
     *
     * @return
     */
    List<ExchangisDataSourceModel> findPage(PageQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<ExchangisDataSourceModel> selectAllList(PageQuery pageQuery);

}
