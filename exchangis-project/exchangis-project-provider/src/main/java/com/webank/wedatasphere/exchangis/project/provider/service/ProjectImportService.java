package com.webank.wedatasphere.exchangis.project.provider.service;

import com.webank.wedatasphere.exchangis.job.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/15 10:01
 */
public interface ProjectImportService {

    Message importProject(HttpServletRequest req, Map<String, Object> params) throws ExchangisJobServerException;

    IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix, String userName) throws ExchangisJobServerException;
}
