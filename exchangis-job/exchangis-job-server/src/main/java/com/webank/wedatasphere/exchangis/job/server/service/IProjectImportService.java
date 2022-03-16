package com.webank.wedatasphere.exchangis.job.server.service;

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
public interface IProjectImportService {
    Message importProject(HttpServletRequest req, Map<String, String> params) throws ExchangisJobServerException, ServerException;

    IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix);
}
