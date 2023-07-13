package com.webank.wedatasphere.exchangis.project.server.service;

import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import java.rmi.ServerException;
import java.util.Map;
import java.util.Set;

/**
 * @author tikazhang
 * @Date 2022/3/15 9:30
 */
public interface ProjectExportService {

    /**
     * Export exchangis job to BML.
     *
     * @param username params
     * @return
     */
    Message exportProject(Map<String, Object> params, String username, HttpServletRequest request) throws ExchangisJobServerException, ServerException;

    ExportedProject export(Long projectId, Map<String, Set<Long>> moduleIdsMap, boolean partial, HttpServletRequest request) throws ExchangisJobServerException;

    Map<String, Set<Long>> getModuleIdsMap(Map<String, Object> params);

}
