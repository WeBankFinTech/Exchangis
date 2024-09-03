package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface DataSourceModelMapper {

    /**
     * Select model by id and version, then lock for update
     * @param id model id
     * @return
     */
    DataSourceModel selectForUpdate(@Param("id") Long id, @Param("version") Long version);

    /**
     * Select one by datasource id
     * @param dataSourceId data source id
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
     * Delete the duplicated models under model id
     * @param modelId model id
     * @return affect rows
     */
    int deleteDuplicate(Long modelId);
    /**
     * Update
     *
     * @param dataSourceModel dataSourceModelEntity
     * @return affect rows
     */
    int update(DataSourceModel dataSourceModel);

    /**
     * Update in version
     * @param dataSourceModel model
     * @return
     */
    int updateInVersion(DataSourceModel dataSourceModel);
    /**
     * Update version(increase the version)
     * @param id model id
     */
    int updateVersion(Long id);

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
     * @param id primary key
     * @return data
     */
    DataSourceModel selectOne(Long id);

    /**
     * Search
     *
     * @return
     */
    List<DataSourceModel> queryPageList(PageQuery pageQuery);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<DataSourceModel> selectAllList(PageQuery pageQuery);

}
