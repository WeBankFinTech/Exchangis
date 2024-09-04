package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.function.Consumer;

/**
 * Open for other module to invoke
 */
public interface ProjectOpenService {

    /**
     * Get project info
     * @param projectId project id
     * @return info entity
     */
    ExchangisProjectInfo getProject(Long projectId);
    /**
     * If it has authority
     * @param username username
     * @param projectId project id
     * @param operationType operation type
     * @return bool
     */
    boolean hasAuthority(String username, Long projectId, OperationType operationType);

    boolean hasAuthority(String username, ExchangisProjectInfo project, OperationType operationType);

    /**
     * If it has authority of data source related by project
     * @param username username
     * @param dataSourceId da
     * @param authority call back function
     * @return boolean
     */
    boolean hasDataSourceAuth(String username, Long dataSourceId, Consumer<ExchangisDataSource> authority);

    /**
     * Page query the data source relation
     * @param queryVo query vo
     * @return result
     */
    PageResult<ExchangisProjectDsRelation> queryDsRelation(ProjectDsQueryVo queryVo);

    /**
     * Add data source relations
     * @param relations relations
     */
    void addDsRelations(List<ExchangisProjectDsRelation> relations);

    /**
     * List related data sources
     * @param proIds project ids
     * @return type
     */
    List<ExchangisProjectDsRelation> listByProjects(List<Long> proIds);

    /**
     * Recycle user project
     * @param username
     * @param handover
     */
    void recycleUserProject(@Param("username") String username, @Param("handover") String handover,
                            @Param("projectIds") List<Long> projectIds);
}
