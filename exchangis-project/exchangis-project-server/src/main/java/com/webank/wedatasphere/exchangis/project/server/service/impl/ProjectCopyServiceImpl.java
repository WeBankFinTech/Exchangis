package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.server.restful.external.ModuleEnum;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectCopyService;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectImportService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author tikazhang
 * @Date 2022/4/24 21:15
 */
@Service
public class ProjectCopyServiceImpl implements ProjectCopyService {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectCopyServiceImpl.class);

    @Autowired
    private ProjectImportService projectImportService;

    @Autowired
    private ProjectExportServiceImpl projectExportService;

    @Autowired
    private ProjectImportServerImpl projectImportServer;

    @Resource
    private ExchangisJobEntityDao jobEntityDao;

    @Override
    public Message copy(Map<String, Object> params, String userName, HttpServletRequest request) throws ExchangisJobException, ExchangisJobServerException {
        LOG.info("begin to copy in project params is {}", params);
        //Long projectId = Long.parseLong(params.get("projectId").toString());
        Boolean partial = (Boolean) params.get("partial");
        Map<String, Set<Long>> moduleIdsMap = projectExportService.getModuleIdsMap(params);

        Set<Long> longs = moduleIdsMap.get(Objects.isNull(params.get("dataXIds")) ? ModuleEnum.SQOOP_IDS.getName() : ModuleEnum.DATAX_IDS.getName());
        List<Long> list1 = new ArrayList<Long>(longs);
        ExchangisJobEntity exchangisJob = this.jobEntityDao.getBasicInfo(list1.get(0));
        Long projectId = exchangisJob.getProjectId();

        String projectVersion = params.getOrDefault("projectVersion", "v1").toString();
        String flowVersion = (String) params.get("flowVersion");
        if (StringUtils.isEmpty(flowVersion)) {
            LOG.error("flowVersion is null, can not copy flow to a newest version");
            flowVersion = "v00001";
        }
        String contextIdStr = (String) params.get("contextID");

        ExportedProject exportedProject = projectExportService.export(projectId, moduleIdsMap, partial, request);

        copySqoop(moduleIdsMap, exportedProject);

        String projectJson = null;
        try {
            projectJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(exportedProject);
        } catch (JsonProcessingException e) {
            LOG.error("Occur error while tranform class", e.getMessage());
        }
        String versionSuffix = projectVersion + "_" + flowVersion;

        IdCatalog idCatalog = projectImportServer.importOpt(projectJson, projectId, versionSuffix, userName, "copy");

        Message message = Message.ok()
                .data("sqoop", idCatalog.getSqoop());
        return message;
    }

    private void copySqoop(Map<String, Set<Long>> moduleIdsMap, ExportedProject exportedProject) {
        Set<Long> sqoopIds = moduleIdsMap.get(ModuleEnum.SQOOP_IDS.getName());
        if (!sqoopIds.isEmpty()) {
            ExchangisJobVo sqoops = exportedProject.getSqoops().get(0);
            exportedProject.setSqoops(Lists.newArrayList(sqoops));
        }
    }

}
