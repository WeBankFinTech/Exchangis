package com.webank.wedatasphere.exchangis.project.server.service;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/4/24 21:11
 */
public interface ProjectCopyService {

    /**
     * Copy node
     * @param
     */
    Message copy(Map<String, Object> params, String UserName, HttpServletRequest request) throws ExchangisJobException, ExchangisJobServerException;
}
