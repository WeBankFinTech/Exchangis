package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;

/**
 * Open for other module to invoke
 */
public interface ProjectOpenService {

    /**
     * If has authority
     * @param username username
     * @param projectId project id
     * @param operationType operation type
     * @return bool
     */
    boolean hasAuthority(String username, Long projectId, OperationType operationType);

}
