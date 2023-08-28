package com.webank.wedatasphere.exchangis.project.server.service;

import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import java.rmi.ServerException;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/15 10:01
 */
public interface ProjectImportService {
    Message importProject(HttpServletRequest req, Map<String, Object> params) throws ExchangisJobServerException, ServerException;

    IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix, String userName, String importType) throws ExchangisJobServerException;
}
