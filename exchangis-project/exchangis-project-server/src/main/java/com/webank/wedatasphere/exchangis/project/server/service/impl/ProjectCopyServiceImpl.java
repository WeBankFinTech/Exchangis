package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectCopyService;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectImportService;
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
import java.util.stream.Collectors;

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
    private ProjectExportServiceImpl exportService;

    @Autowired
    private ProjectImportServerImpl importService;

    @Resource
    private ExchangisJobEntityDao jobEntityDao;

    @Override
    public Message copy(Map<String, Object> params, String userName, HttpServletRequest request) throws ExchangisJobException, ExchangisJobServerException {
        long projectId = -1L;
        if (Objects.nonNull(params.get("projectId"))) {
            projectId = Long.parseLong(params.get("projectId").toString());
        }
        Boolean partial = (Boolean) params.get("partial");
        List<Long> jobIds = new ArrayList<>();
        Optional.ofNullable(params.get("jobIds")).ifPresent(ids -> jobIds.addAll(Arrays.stream(StringUtils.split(String.valueOf(ids), ","))
                .map(Long::parseLong).collect(Collectors.toSet())));
        if (projectId <= 0) {
            ExchangisJobEntity exchangisJob = this.jobEntityDao.getBasicInfo(jobIds.get(0));
            if (Objects.isNull(exchangisJob)) {
                throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(),
                        "Fail to get job with id: [" + jobIds.get(0) + "], (找不到符合的任务)");
            }
            projectId = exchangisJob.getProjectId();
        }
        String projectVersion = params.getOrDefault("projectVersion", "v1").toString();
        String flowVersion = (String) params.get("flowVersion");
        if (StringUtils.isEmpty(flowVersion)) {
            LOG.error("flowVersion is null, can not copy flow to a newest version");
            flowVersion = "v00001";
        }
        ExportedProject exportedProject = exportService.export(projectId, jobIds, partial, request);
        String projectJson;
        try {
            projectJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(exportedProject);
        } catch (Exception e) {
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.EXPORT_JOB_ERROR.getCode(),
                    "Fail to serialize export job content(序列化导出任务失败）", e);
        }
        String versionSuffix = projectVersion + "_" + flowVersion;
        // Invoke import operation
        IdCatalog idCatalog = importService.importOpt(projectJson,
                projectId, versionSuffix, userName);

        return Message.ok()
                .data("copyRefIds", idCatalog.getJobIds());
    }

}
