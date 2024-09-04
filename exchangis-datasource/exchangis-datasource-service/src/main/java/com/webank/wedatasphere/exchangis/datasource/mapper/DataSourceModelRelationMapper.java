package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelation;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelationDTO;
import com.webank.wedatasphere.exchangis.datasource.core.domain.DataSourceModelRelationQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Relation mapper
 */
public interface DataSourceModelRelationMapper {

    /**
     * Query data source ids by model
     */
    List<Long> queryDsIdsByModel(@Param("modelId") Long modelId);

    /**
     * Query relations by dsIds
     * @param dsIds
     * @return
     */
    List<DataSourceModelRelationDTO> queryRefRelationsByDsIds(@Param("dsIds") List<Long> dsIds);

    /**
     * Query (refer) relations by model
     * @param modelId
     * @return
     */
    List<DataSourceModelRelation> queryRefRelationsByModel(@Param("modelId")Long modelId);

    /**
     * Query data source relations
     * @param dataSourceModelRelationQuery query
     */
    DataSourceModelRelationDTO queryRelations(DataSourceModelRelationQuery dataSourceModelRelationQuery);

    /**
     * Add data source model relation
     * @param dataSourceModelRelations relations
     */
    void addRelation(@Param("list") List<DataSourceModelRelation> dataSourceModelRelations);

    /**
     * Transfer model relation
     * @param fromId from id
     * @param toId to id
     */
    void transferModelRelation(@Param("fromId") Long fromId, @Param("toId") Long toId);

    /**
     * Delete relation by data source name and version
     * @param dsName data source name
     * @param versionId version id
     */
    void deleteRelations(@Param("dsName") String dsName,
                         @Param("dsVersion")Long versionId);

    /**
     * Delete relations by model id
     * @param modelIds model id
     */
    void deleteRelationByModelIds(@Param("ids")List<Long> modelIds);

    /**
     * Delete relations by ds id
     * @param dsId data source id
     */
    void deleteRelationsByDsId(Long dsId);

    /**
     * Delete refer relations by model id
     * @param modelId model id
     */
    void deleteRefRelationByModelId(Long modelId);

    /**
     * Recycle user
     * @param userName userName
     * @param handover handover
     */
    void recycleDsModelRelation(@Param("userName") String userName,
                                @Param("handover") String handover);

}
