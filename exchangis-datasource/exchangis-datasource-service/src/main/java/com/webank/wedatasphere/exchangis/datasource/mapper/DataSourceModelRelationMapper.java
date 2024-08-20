package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDsModelRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Relation mapper
 */
public interface DataSourceModelRelationMapper {

    /**
     * query dataSourceModelBinds
     */
    List<ExchangisDsModelRelation> queryDataSourceModelBinds();

    /**
     * query dataSourceModelBinds
     */
    List<Long> queryDataSourceIdsByModel(@Param("modelId") Long modelId);

    /**
     * query dataSourceModelBind
     * @param dataSourceModelBind
     */
    ExchangisDsModelRelation queryDataSourceModelBind(ExchangisDsModelRelation dataSourceModelBind);

    /**
     * add dataSourceModelBinds
     * @param dataSourceModelBinds
     */
    void addDataSourceModelBind(@Param("dataSourceModelBinds") List<ExchangisDsModelRelation> dataSourceModelBinds);

    /**
     * update dataSourceModelBinds
     * @param dataSourceModelBinds
     */
    void updateDataSourceModelBind(@Param("dataSourceModelBinds") List<ExchangisDsModelRelation> dataSourceModelBinds);

    /**
     * delete dataSourceModelBinds
     * @param modelId
     */
    void deleteDataSourceModelBind(Long modelId, Long dataSourceId);
}
