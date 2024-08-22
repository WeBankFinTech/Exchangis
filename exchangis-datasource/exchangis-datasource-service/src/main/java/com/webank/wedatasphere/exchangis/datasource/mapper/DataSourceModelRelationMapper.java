package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelation;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelationQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Relation mapper
 */
public interface DataSourceModelRelationMapper {

    /**
     * query dataSourceModelRelations
     */
    List<DataSourceModelRelation> querydsModelRelations();

    /**
     * query dataSourceModelBinds
     */
    List<Long> queryDsIdsByModel(@Param("modelId") Long modelId);

    /**
     * query dataSourceModelRelation
     * @param dataSourceModelRelationQuery
     */
    DataSourceModelRelation queryDsModelRelation(DataSourceModelRelationQuery dataSourceModelRelationQuery);

    /**
     * add dataSourceModelRelations
     * @param dataSourceModelRelations
     */
    void addDsModelRelation(@Param("dataSourceModelRelations") List<DataSourceModelRelation> dataSourceModelRelations);

    /**
     * update dataSourceModelRelations
     * @param dataSourceModelRelations
     */
    void updateDsModelRelation(@Param("dataSourceModelRelations") List<DataSourceModelRelation> dataSourceModelRelations);

    /**
     * delete dataSourceModelRelations
     * @param modelId
     */
    void deleteDsModelRelation(Long modelId, Long dataSourceId);
}
