package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelBinding;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataSourceModelBindMapper {

    /**
     * query dataSourceModelBinds
     */
    List<DataSourceModelBinding> queryDataSourceModelBinds();

    /**
     * query dataSourceModelBinds
     */
    List<Long> queryDataSourceIdsByModel(@Param("modelId") Long modelId);

    /**
     * query dataSourceModelBind
     * @param dataSourceModelBinding
     */
    DataSourceModelBinding queryDataSourceModelBind(DataSourceModelBinding dataSourceModelBinding);

    /**
     * add dataSourceModelBinds
     * @param dataSourceModelBindings
     */
    void addDataSourceModelBind(@Param("dataSourceModelBinds") List<DataSourceModelBinding> dataSourceModelBindings);

    /**
     * update dataSourceModelBinds
     * @param dataSourceModelBindings
     */
    void updateDataSourceModelBind(@Param("dataSourceModelBinds") List<DataSourceModelBinding> dataSourceModelBindings);

    /**
     * delete dataSourceModelBinds
     * @param modelId
     */
    void deleteDataSourceModelBind(Long modelId, Long dataSourceId);
}
