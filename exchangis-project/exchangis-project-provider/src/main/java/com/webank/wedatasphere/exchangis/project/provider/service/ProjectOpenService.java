package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;

/**
 * Open for other module to invoke
 */
public interface ProjectOpenService {

    /**
     * If it has authority
     * @param username username
     * @param projectId project id
     * @param operationType operation type
     * @return bool
     */
    boolean hasAuthority(String username, Long projectId, OperationType operationType);

    /**
     * Page query the data source relation
     * @param queryVo query vo
     * @return result
     */
    PageResult<ExchangisProjectDsRelation> queryDsRelation(ProjectDsQueryVo queryVo);
}
