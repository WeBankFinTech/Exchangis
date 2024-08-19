package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModelBind;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jefftlin
 * @date 2024/8/15
 */
public interface DataSourceModelBindMapper {

    /**
     * query dataSourceModelBinds
     */
    List<DataSourceModelBind> queryDataSourceModelBinds();

    /**
     * query dataSourceModelBinds
     */
    List<Long> queryDataSourceIdsByModel(@Param("modelId") Long modelId);

    /**
     * query dataSourceModelBind
     * @param dataSourceModelBind
     */
    DataSourceModelBind queryDataSourceModelBind(DataSourceModelBind dataSourceModelBind);

    /**
     * add dataSourceModelBinds
     * @param dataSourceModelBinds
     */
    void addDataSourceModelBind(@Param("dataSourceModelBinds") List<DataSourceModelBind> dataSourceModelBinds);

    /**
     * update dataSourceModelBinds
     * @param dataSourceModelBinds
     */
    void updateDataSourceModelBind(@Param("dataSourceModelBinds") List<DataSourceModelBind> dataSourceModelBinds);

    /**
     * delete dataSourceModelBinds
     * @param modelId
     */
    void deleteDataSourceModelBind(Long modelId, Long dataSourceId);
}
