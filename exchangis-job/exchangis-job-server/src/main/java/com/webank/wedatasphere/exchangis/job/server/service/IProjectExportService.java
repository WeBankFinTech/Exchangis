package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * @author tikazhang
 * @Date 2022/3/15 9:30
 */
public interface IProjectExportService {
    String exportProject(Map<String, String> params, String username, HttpServletRequest request) throws Exception;

    ExportedProject export(Long projectId, Map<String, Set<Long>> moduleIdsMap, boolean partial);

    Map<String, Set<Long>> getModuleIdsMap(Map<String, String> params);

    Long getProjectId(Map<String, Set<Long>> moduleIdsMap);
}
