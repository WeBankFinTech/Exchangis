package com.webank.wedatasphere.exchangis.project.server.restful.external;

import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectCopyService;
import com.webank.wedatasphere.exchangis.project.server.service.impl.ProjectExportServiceImpl;
import com.webank.wedatasphere.exchangis.project.server.service.impl.ProjectImportServerImpl;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.rmi.ServerException;
import java.util.Map;

/**
 * Define to support the app conn, in order to distinguish from the inner api
 */
@RestController
@RequestMapping(value = "/dss/exchangis/main/appJob", produces = {"application/json;charset=utf-8"})
public class ExchangisProjectJobDssAppConnRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectJobDssAppConnRestfulApi.class);

    @Resource
    private ProjectImportServerImpl projectImportServer;

    @Resource
    private ProjectExportServiceImpl projectExportService;

    @Resource
    private ProjectCopyService projectCopyService;

    @Autowired
    private ProjectMapper projectMapper;

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Message importJob(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException, ExchangisJobServerException {

        Message response = null;
        String userName = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        try {
            LOG.info("param: {}", params);
            /*if (!hasAuthority(userName, jobInfoService.getJob(((Integer) params.get("sqoopIds")).longValue(), true))) {
                return Message.error("You have no permission to import (没有导入权限)");
            }*/
            response = projectImportServer.importProject(request, params);
            LOG.info("import job success");
        } catch (ExchangisJobServerException e) {
            String message = "Fail import job [ id: " + params + "] (导入任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, userName, TargetTypeEnum.JOB, "", "Export parameter is: " + params.toString(), OperateTypeEnum.IMPORT, request);
        return response;

    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public Message exportJob(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException, ExchangisJobServerException {
        String userName = UserUtils.getLoginUser(request);

        LOG.info("export function params: {}", params);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        Message response = null;
        try {
            /*if (!hasAuthority(userName, jobInfoService.getJob(((Integer) params.get("sqoopIds")).longValue(), true))) {
                return Message.error("You have no permission to export (没有导出权限)");
            }*/
            response = projectExportService.exportProject(params, userName, request);
            LOG.info("export job success");
        } catch (Exception e) {
            String message = "Fail Export job [ id: " + params + "] (导出任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, userName, TargetTypeEnum.JOB, "", "Export parameter is: " + params.toString(), OperateTypeEnum.EXPORT, request);
        return response;
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public Message copy(@Context HttpServletRequest request, @RequestBody Map<String, Object> params) throws ServerException {
        String userName = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);

        LOG.info("copy function params: {}", params);
        Message response = null;
        try {
            response = projectCopyService.copy(params, userName, request);
            LOG.info("copy node success");
        } catch (Exception e) {
            String message = "Fail Copy project [ id: " + params + "] (导出任务失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        AuditLogUtils.printLog(oringinUser, userName, TargetTypeEnum.JOB, "", "Copy parameter is: " + params.toString(), OperateTypeEnum.COPY, request);
        return response;
    }
}
