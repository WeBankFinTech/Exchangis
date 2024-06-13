package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;

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
     * Page query the data source relation
     * @param queryVo query vo
     * @return result
     */
    PageResult<ExchangisProjectDsRelation> queryDsRelation(ProjectDsQueryVo queryVo);
}
