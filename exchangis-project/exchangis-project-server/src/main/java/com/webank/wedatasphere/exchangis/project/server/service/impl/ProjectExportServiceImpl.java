package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import org.apache.linkis.bml.client.BmlClientFactory;
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
import java.rmi.ServerException;
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
    public Message exportProject(Map<String, Object> params, String userName, HttpServletRequest request) throws ExchangisJobServerException, ServerException {
        ExportedProject exportedProject = null;
        Long projectId = Long.parseLong(params.get("projectId").toString());
        Boolean partial = (Boolean) params.get("partial");
        Map<String, Set<Long>> moduleIdsMap = getModuleIdsMap(params);

        LOG.info("export project, user: {}, project: {}, partial:{}", userName, projectId, partial);
        exportedProject = export(projectId, moduleIdsMap, partial, request);
        String exported = null;
        try {
            exported = BDPJettyServerHelper.jacksonJson().writeValueAsString(exportedProject);
        } catch (JsonProcessingException e) {
            LOG.error("Occur error while tranform class", e.getMessage());
        }

        LOG.info("projectName: {}, exported:{}", exportedProject.getName(), exported);
        BmlClient bmlClient = BmlClientFactory.createBmlClient(userName);
        BmlUploadResponse bmlUploadResponse = bmlClient.uploadShareResource(userName, exportedProject.getName(),
                "exchangis_exported_" + UUID.randomUUID(), new ByteArrayInputStream(exported.getBytes(StandardCharsets.UTF_8)));

        if (bmlUploadResponse == null || !bmlUploadResponse.isSuccess()) {
            throw new ServerException("cannot upload exported data to BML");
        }

        LOG.info("{} is exporting the project, uploaded to BML the resourceID is {} and the version is {}",
                userName, bmlUploadResponse.resourceId(), bmlUploadResponse.version());

        Message message = Message.ok("export job")
                .data("resourceId", bmlUploadResponse.resourceId())
                .data("version", bmlUploadResponse.version());
        return message;
    }

    @Override
    public ExportedProject export(Long projectId, Map<String, Set<Long>> moduleIdsMap, boolean partial, HttpServletRequest request) throws ExchangisJobServerException {
        ExportedProject exportedProject = new ExportedProject();
        ExchangisProjectInfo project = projectService.getProjectDetailById(projectId);

        LOG.info("execute export method! export project is {}.", project.getName());
        exportedProject.setName(project.getName());

        setSqoop(projectId, moduleIdsMap, partial, exportedProject, request);

        setDatax(projectId, moduleIdsMap, partial, exportedProject, request);

        return exportedProject;
    }

    private void setSqoop(Long projectId, Map<String, Set<Long>> moduleIdsMap, boolean partial, ExportedProject exportedProject, HttpServletRequest request) throws ExchangisJobServerException {
        List<ExchangisJobVo> sqoops = new ArrayList<>();
        LOG.info("Request: {}", request);
        if (partial) {
            Set<Long> longs = moduleIdsMap.get(ModuleEnum.SQOOP_IDS.getName());
            if (longs.size() > 0) {
                for (Long id : longs) {
                    LOG.info("id: {}", id);
                    ExchangisJobVo job = jobInfoService.getJob(id, false);

                    String sqoopStr = null;
                    try {
                        sqoopStr = BDPJettyServerHelper.jacksonJson().writeValueAsString(job);
                    } catch (JsonProcessingException e) {
                        LOG.error("Occur error while tranform class", e.getMessage());
                    }

                    LOG.info("sqoopStr:{}", sqoopStr);
                    LOG.info("ExchangisJobVo sqoop: {}", job.getContent());
                    LOG.info("getCreateTime: {}", job.getId());
                    LOG.info("executorUser: {}", job.getExecuteUser());
                    sqoops.add(job);
                }
                exportedProject.setSqoops(sqoops);
            }

        } else {
            LOG.info("Through request {} and projectId {} get Sqoopjob", request, projectId);
            sqoops = jobInfoService.getSubJobList(request, projectId);
            exportedProject.setSqoops(sqoops);
            //exportedProject.setSqoops(jobInfoService.getByProject(request, projectId));
        }
        LOG.info("exporting project, export sqoopJob: {}", exportedProject);
    }

    private void setDatax(Long projectId, Map<String, Set<Long>> moduleIdsMap, boolean partial, ExportedProject exportedProject, HttpServletRequest request) throws ExchangisJobServerException {
        List<ExchangisJobVo> dataxs = new ArrayList<>();
        LOG.info("Request: {}", request);
        if (partial) {
            Set<Long> longs = moduleIdsMap.get(ModuleEnum.DATAX_IDS.getName());
            if (longs.size() > 0) {
                for (Long id : longs) {
                    LOG.info("id: {}", id);
                    ExchangisJobVo job = jobInfoService.getJob(id, false);

                    String dataxStr = null;
                    try {
                        dataxStr = BDPJettyServerHelper.jacksonJson().writeValueAsString(job);
                    } catch (JsonProcessingException e) {
                        LOG.error("Occur error while tranform class", e.getMessage());
                    }

                    LOG.info("dataxStr:{}", dataxStr);
                    LOG.info("ExchangisJobVo sqoop: {}", job.getContent());
                    LOG.info("getCreateTime: {}", job.getId());
                    LOG.info("executorUser: {}", job.getExecuteUser());
                    dataxs.add(job);
                }
                exportedProject.setDataxes(dataxs);
            }

        } else {
            LOG.info("Through request {} and projectId {} get Dataxjob", request, projectId);
            dataxs = jobInfoService.getSubJobList(request, projectId);
            exportedProject.setSqoops(dataxs);
        }
        LOG.info("exporting project, export dataxJob: {}", exportedProject);

    }

    /**
     * 获取需要导出对象集合
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Set<Long>> getModuleIdsMap(Map<String, Object> params) {

        Map<String, Set<Long>> map = Maps.newHashMap();
        String sqoopIdsStr = null;
        if(params.get("sqoopIds") != null) {
            sqoopIdsStr = params.get("sqoopIds").toString();
        }
        String dataxIdsStr = null;
        if(params.get("dataXIds") != null) {
            dataxIdsStr = params.get("dataXIds").toString();
        }

        Set<Long> sqoopIds = Sets.newHashSet();
        Set<Long> dataxIds = Sets.newHashSet();

        if (StringUtils.isNotEmpty(sqoopIdsStr)) {
            sqoopIds = Arrays.stream(StringUtils.split(sqoopIdsStr, ","))
                    .map(Long::parseLong).collect(Collectors.toSet());
        }
        if (StringUtils.isNotEmpty(dataxIdsStr)) {
            dataxIds = Arrays.stream(StringUtils.split(dataxIdsStr, ","))
                    .map(Long::parseLong).collect(Collectors.toSet());
        }
        map.put("sqoopIds", sqoopIds);
        map.put("dataXIds", dataxIds);
        LOG.info("The objects to be exported are: {}", map);
        return map;
    }
}
