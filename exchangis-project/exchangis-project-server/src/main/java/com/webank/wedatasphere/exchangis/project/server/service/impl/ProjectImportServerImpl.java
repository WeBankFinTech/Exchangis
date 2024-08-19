package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.webank.wedatasphere.exchangis.engine.resource.bml.BmlClients;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectImportService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.protocol.BmlDownloadResponse;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tikazhang
 * @Date 2022/3/15 9:58
 */

@Service
public class ProjectImportServerImpl implements ProjectImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectImportServerImpl.class);

    Pattern pattern1 = Pattern.compile("([a-zA-Z]+_\\d+).*");

    Pattern pattern2 = Pattern.compile("(\\S+)_v\\d+\\S+");

    @Resource
    private JobInfoService jobInfoService;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Message importProject(HttpServletRequest req, Map<String, Object> params)
            throws ExchangisJobServerException{
        String userName = SecurityFilter.getLoginUsername(req);
        String resourceId = (String) params.get("resourceId");
        String version = (String) params.get("version");
        Object importProjectId = params.get("projectId");
        Long projectId = null;
        if (importProjectId instanceof Integer) {
            projectId = Integer.valueOf(importProjectId.toString()).longValue();
        } else if (importProjectId instanceof Long) {
            projectId = Long.valueOf(importProjectId.toString());
        } else {
            throw new IllegalArgumentException("Cannot convert " + importProjectId + " to long");
        }
        String projectName = String.valueOf(Optional.ofNullable(params.get("projectName")).orElse(""));
        String projectVersion = (String) params.get("projectVersion");
        String flowVersion = (String) params.get("flowVersion");
        String versionSuffix = projectVersion + "_" + flowVersion;
        BmlClient bmlClient = BmlClients.getInstance();
        BmlDownloadResponse bmlDownloadResponse = bmlClient.downloadShareResource(userName, resourceId, version);
        if (bmlDownloadResponse == null || !bmlDownloadResponse.isSuccess()) {
            throw new ExchangisJobServerException(ExchangisJobExceptionCode.EXPORT_JOB_ERROR.getCode(),
                    "Cannot download imported data/resource from BML (从BML下载导入资源失败)", null);
        }
        LOG.info("Download jobs resource for project: [{}] " +
                "from BML: [resourceId: {}, version: {}]", projectName, bmlDownloadResponse.resourceId(), bmlDownloadResponse.version());
        Message message;
        try {
            String projectJson = IOUtils.toString(bmlDownloadResponse.inputStream(), StandardCharsets.UTF_8);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Download jobs resources: {}", projectJson);
            }
            IdCatalog idCatalog = importOpt(projectJson, projectId, versionSuffix, userName);
            message = Message.ok("import Job ok")
                    .data("importRefIds", idCatalog.getJobIds());
            return message;
        } catch (IOException | ExchangisJobServerException e) {
            LOG.error("Error occur while import option: {}", e.getMessage());
            message = Message.error("Error occur while import option: [" + e.getMessage() + "]");
        }
        finally {
            IOUtils.closeQuietly(bmlDownloadResponse.inputStream());
        }
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix, String userName) throws ExchangisJobServerException {
        ExportedProject exportedProject = BDPJettyServerHelper.gson().fromJson(projectJson, ExportedProject.class);
        IdCatalog idCatalog = new IdCatalog();
        if (!exportedProject.getJobs().isEmpty()) {
            importJob(userName, exportedProject, idCatalog, projectId, versionSuffix);
        }
        return idCatalog;
    }

    /**
     * Import job
     * @param username username
     * @param exportedProject exported project
     * @param idCatalog id catalog
     * @param projectId project id
     * @param versionSuffix version suffix
     * @throws ExchangisJobServerException e
     */
    private void importJob(String username, ExportedProject exportedProject, IdCatalog idCatalog,
                           Long projectId, String versionSuffix) throws ExchangisJobServerException {
        List<ExchangisJobVo> jobs = exportedProject.getJobs();
        ExchangisProject importProj;
        if (Objects.nonNull(projectId)) {
            // Fetch import project
            importProj = projectMapper.getDetailById(projectId);
        } else {
            // Try to fetch projects with name and use the first one
            List<ExchangisProject> results = projectMapper.getDetailByName(exportedProject.getName());
            if (results.isEmpty()) {
                importProj = new ExchangisProject();
                importProj.setName(exportedProject.getName());
                importProj.setCreateTime(Calendar.getInstance().getTime());
                importProj.setCreateUser(username);
                projectMapper.insertOne(importProj);
            } else if (results.size() == 1){
                importProj = results.get(0);
            } else {
                throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(),
                        "Already exits duplicated project name(存在重复项目名称) projectName is:" + "[" + exportedProject.getName() + "]");
            }
        }
        // Redefine the project id
        projectId = importProj.getId();
        for (ExchangisJobVo job : jobs){
            Long prevId = job.getId();
            // Reset the project id
            job.setProjectId(String.valueOf(projectId));
            job.setJobName(updateName(job.getJobName(), versionSuffix));
            List<ExchangisJobVo> existedJobs = jobInfoService.getByNameWithProjectId(job.getJobName(), projectId);
            if (!existedJobs.isEmpty()){
                throw new ExchangisJobServerException(ExchangisJobExceptionCode.JOB_EXCEPTION_CODE.getCode(),
                        "Already exits duplicated job name(存在重复任务名称) jobName is:" + "[" + job.getJobName() + "]");
            }
            jobInfoService.createJob(job);
            LOG.info("Success to import job: [name: {}, import id: {}, export id: {}]",
                    job.getJobName(), job.getId(), prevId);
            idCatalog.getJobIds().put(prevId, job.getId());
        }
    }


    /**
     * Update name
     * @param name name
     * @param versionSuffix version suffix
     * @return new name
     */
    private String updateName(String name, String versionSuffix) {
        if (StringUtils.isBlank(versionSuffix)) {
            return name;
        }

        Matcher matcher = pattern1.matcher(name);
        if (matcher.find()) {
            return matcher.group(1) + "_" + versionSuffix;
        } else {
            Matcher matcher2 = pattern2.matcher(name);
            if (matcher2.find()) {
                return matcher2.group(1) + "_" + versionSuffix;
            }
        }
        return name + "_" + versionSuffix;
    }
}
