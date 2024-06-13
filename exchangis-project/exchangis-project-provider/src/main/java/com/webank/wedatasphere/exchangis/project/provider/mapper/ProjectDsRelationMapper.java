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
     * @return type
     */
    List<ExchangisProjectDsRelation> listByProject(Long projectId, String dsType);
}
