package com.webank.wedatasphere.exchangis.project.provider.mapper;

import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectDsRelationMapper {
    /**
     * Batch insert
     * @param relations relations
     */
    void insertBatch(List<ExchangisProjectDsRelation> relations);

    /**
     * Delete by ids
     * @param idList id list
     */
    void deleteByIds(List<Long> idList);

    /**
     * Delete by project
     * @param projectId project id
     */
    void deleteByProject(Long projectId);

    /**
     * Fetch related data sources in page
     * @param queryVo query
     * @return
     */
    List<ExchangisProjectDsRelation> queryPageList(ProjectDsQueryVo queryVo);

    /**
     * List related data sources
     * @param projectId project id
     * @param dsType datasource type
     * @return type
     */
    List<ExchangisProjectDsRelation> listByProject(@Param("projectId") Long projectId, @Param("dsType") String dsType);

    /**
     * List related data sources
     * @param proIds project ids
     * @return type
     */
    List<ExchangisProjectDsRelation> listByProjects(@Param("proIds") List<Long> proIds);

    /**
     * Get data source by id and username which in project privilege table
     * @param username username
     * @param dsId data source id
     * @return ds
     */
    ExchangisProjectDsRelation getByUserAndDsId(@Param("username") String username, @Param("dsId") Long dsId);
}
