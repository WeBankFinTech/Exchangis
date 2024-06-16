package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.exchangis.engine.resource.bml.BmlClients;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.restful.external.ModuleEnum;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectExportService;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jefftlin
 * @date 2023/7/13
 */
@Service
public class ProjectExportServiceImpl implements ProjectExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectExportServiceImpl.class);

    /**
     * Project service
     */
    @Resource
    private ProjectService projectService;

    @Resource
    private JobInfoService jobInfoService;

    @Override
    public Message exportProject(Map<String, Object> params, String userName, HttpServletRequest request) throws
            ExchangisJobServerException {
        ExportedProject exportedProject;
        Long projectId = Long.parseLong(params.get("projectId").toString());
        Boolean partial = (Boolean) params.get("partial");
        List<Long> jobIds = new ArrayList<>();
        Optional.ofNullable(params.get("jobIds")).ifPresent(ids -> jobIds.addAll(Arrays.stream(StringUtils.split(String.valueOf(ids), ","))
                .map(Long::parseLong).collect(Collectors.toSet())));
        exportedProject = export(projectId, jobIds, partial, request);
        String exported;
        try {
            exported = BDPJettyServerHelper.jacksonJson().writeValueAsString(exportedProject);
        } catch (Exception e) {
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.EXPORT_JOB_ERROR.getCode(),
                    "Fail to serialize export job content(序列化导出任务失败）", e);
        }
        if (LOG.isDebugEnabled()){
            LOG.debug("Export jobs in project: [{}], content: {}", exportedProject.getName(), exported);
        }
        // TODO abstract bml client to common module
        BmlClient bmlClient = BmlClients.getInstance();
        BmlUploadResponse bmlUploadResponse = bmlClient.uploadShareResource(userName, exportedProject.getName(),
                "exchangis_exported_" + UUID.randomUUID(), new ByteArrayInputStream(exported.getBytes(StandardCharsets.UTF_8)));
        if (bmlUploadResponse == null || !bmlUploadResponse.isSuccess()) {
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.EXPORT_JOB_ERROR.getCode(),
                    "Cannot upload exported data to BML (导出数据上传BML失败)", null);
        }
        LOG.info("User [{}] success to upload the exported project jobs to BML , resourceID is [{}] and the version is [{}]",
                userName, bmlUploadResponse.resourceId(), bmlUploadResponse.version());
        return Message.ok("export job")
                .data("resourceId", bmlUploadResponse.resourceId())
                .data("version", bmlUploadResponse.version());
    }

    @Override
    public ExportedProject export(Long projectId, List<Long> jobIds, boolean partial, HttpServletRequest request) throws ExchangisJobServerException {
        ExportedProject exportedProject = new ExportedProject();
        ExchangisProjectInfo project = projectService.getProjectDetailById(projectId);
        exportedProject.setName(project.getName());
        // Export jobs in project id
        if (!jobIds.isEmpty()){
            exportJobs(project, jobIds, partial, exportedProject, request);
        }
        return exportedProject;
    }

    /**
     * Export jobs under project
     * @param project project
     * @param jobIds job ids
     * @param partial partial (if false to export all jobs)
     * @param exportedProject exported project
     * @param request request
     * @throws ExchangisJobServerException
     */
    private void exportJobs(ExchangisProjectInfo project, List<Long> jobIds, boolean partial,
                            ExportedProject exportedProject, HttpServletRequest request) throws ExchangisJobServerException {
        List<ExchangisJobVo> jobs = new ArrayList<>();
        if (partial) {
            LOG.info("Export sqoop jobs: [{}] in project: [{}]", StringUtils.join(jobIds, ","), project.getName());
            for (Long id : jobIds) {
                ExchangisJobVo job = jobInfoService.getJob(id, false);
                try {
                    BDPJettyServerHelper.jacksonJson().writeValueAsString(job);
                } catch (Exception e) {
                    throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(),
                            "Fail to serialize content of job: [" + job.getJobName() + "], engine_type: [" + job.getEngineType() + "]", e);
                }
                jobs.add(job);
            }
        } else {
            LOG.info("Export all jobs in project : [{}]", project.getName());
            jobs = jobInfoService.getSubJobList(request, Long.valueOf(project.getId()));
        }
        exportedProject.setJobs(jobs);
    }


}
