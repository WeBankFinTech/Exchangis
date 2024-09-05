package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.job.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    Message exportProject(Map<String, Object> params, String username, HttpServletRequest request) throws ExchangisJobServerException;

    ExportedProject export(Long projectId, List<Long> jobIds, boolean partial, HttpServletRequest request) throws ExchangisJobServerException;

}
