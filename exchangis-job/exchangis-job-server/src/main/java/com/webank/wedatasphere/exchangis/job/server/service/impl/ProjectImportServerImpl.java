package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.IProjectImportService;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlDownloadResponse;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tikazhang
 * @Date 2022/3/15 9:58
 */

@Service
public class ProjectImportServerImpl implements IProjectImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectImportServerImpl.class);

    Pattern pattern1 = Pattern.compile("([a-zA-Z]+_\\d+).*");

    Pattern pattern2 = Pattern.compile("(\\S+)_v1\\S+");

    @Resource
    private JobInfoService jobInfoService;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Message importProject(HttpServletRequest req, Map<String, Object> params) throws ExchangisJobServerException, ServerException {
        String userName = SecurityFilter.getLoginUsername(req);
        //String resourceId = "99763d27-a35e-43f2-829b-100830bca538";
        String resourceId = (String) params.get("resourceId");
        String version = (String) params.get("flowVersion");
        //Long projectId = Long.parseLong("1497870871035973669");
        Long projectId = Long.parseLong("111111");
        String projectVersion = (String) params.get("projectVersion");
        String flowVersion = (String) params.get("flowVersion");
        String versionSuffix = projectVersion;
        LOG.info("resourceId: {}, projectId: {}, versionSuffix: {}, version: {}, userName: {}", resourceId, projectId, versionSuffix, version, userName);
        BmlClient bmlClient = BmlClientFactory.createBmlClient(userName);
        BmlDownloadResponse bmlDownloadResponse = bmlClient.downloadShareResource(userName, resourceId, version);
        LOG.info("bmlDownloadResponse: {}", bmlDownloadResponse);
        Message message = null;
        if (bmlDownloadResponse == null || !bmlDownloadResponse.isSuccess()) {
            throw new ServerException("cannot download exported data from BML");
        }
        try {
            String projectJson = IOUtils.toString(bmlDownloadResponse.inputStream(), StandardCharsets.UTF_8);
            LOG.info("projectJson: {}", projectJson);
            IdCatalog idCatalog = importOpt(projectJson, projectId, versionSuffix, userName);
            message = Message.ok("import Job ok")
                    .data("sqoop", idCatalog.getSqoop())
                    .data("datax", idCatalog.getDatax());

            return message;
        } catch (IOException | ExchangisJobServerException e) {
            LOG.error("Error occur while import option: {}", e.getMessage());
            message = Message.error("Error occur while import option: {}");
            //throw new ExchangisJobServerException(31101, "导入出现错误:" + "[" + e.getMessage() + "]");
        }
        finally {
            IOUtils.closeQuietly(bmlDownloadResponse.inputStream());
        }

        return message;
    }

    @Override
    public IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix, String userName) throws ExchangisJobServerException {
        ExportedProject exportedProject = BDPJettyServerHelper.gson().fromJson(projectJson, ExportedProject.class);
        IdCatalog idCatalog = new IdCatalog();

        importSqoop(projectId, versionSuffix, exportedProject, idCatalog, userName);

        importDatax(projectId, versionSuffix, exportedProject, idCatalog, userName);

        return idCatalog;
    }

    private void importSqoop(Long projectId, String versionSuffix, ExportedProject exportedProject, IdCatalog idCatalog, String userName) throws ExchangisJobServerException {
        List<ExchangisJobVo> sqoops = exportedProject.getSqoops();
        if (sqoops == null) {
            return;
        }
        List<ExchangisProject> projects = projectMapper.getDetailByName(exportedProject.getName());
        if (projects.size() == 0) {
            ExchangisProject project = new ExchangisProject();
            project.setName(exportedProject.getName());
            project.setCreateTime(Calendar.getInstance().getTime());
            project.setCreateUser(userName);
            Long newProjectId = projectMapper.insertOne(project);
            List<ExchangisProject> newProjects = projectMapper.getDetailByName(exportedProject.getName());
            addSqoopTask (sqoops, newProjects, versionSuffix, idCatalog, projectId);
        }
        else if (projects.size() == 1) {
            addSqoopTask (sqoops, projects, versionSuffix, idCatalog, projectId);
        }
        else {
            throw new ExchangisJobServerException(31101, "Already exits duplicated project name(存在重复项目名称) projectName is:" + "[" + exportedProject.getName() + "]");
        }
    }

    private void importDatax(Long projectId, String versionSuffix, ExportedProject exportedProject, IdCatalog idCatalog, String userName) {
        List<ExchangisJobVo> dataxes = exportedProject.getDataxes();
        if (dataxes == null) {
            return;
        }
        for (ExchangisJobVo datax : dataxes) {
            Long oldId = datax.getId();
            datax.setProjectId(projectId);
            datax.setJobName(updateName(datax.getJobName(), versionSuffix));
            //Long existingId = (long) 66;
            Long existingId = jobInfoService.getByNameWithProjectId(datax.getJobName(), projectId).get(0).getId();
            //Long existingId = jobInfoService.getByNameWithProjectId(datax.getJobName(), projectId);
            if (existingId != null) {
                idCatalog.getSqoop().put(oldId, existingId);
            } else {
                jobInfoService.createJob(datax);
                idCatalog.getSqoop().put(oldId, datax.getId());
            }
        }
    }

    public void addSqoopTask (List<ExchangisJobVo> sqoops, List<ExchangisProject> projects, String versionSuffix, IdCatalog idCatalog, Long projectId) throws ExchangisJobServerException {
        for (ExchangisJobVo sqoop : sqoops) {
            Long projectIdProd = projects.get(0).getId();
            Long oldId = sqoop.getId();
            sqoop.setProjectId(projectIdProd);
            sqoop.setJobName(updateName(sqoop.getJobName(), versionSuffix));
            //Long existingId = (long) 55;
            LOG.info("oldId: {}, projectid: {}, jobName: {}", sqoop.getId(), sqoop.getProjectId(), sqoop.getJobName());
            LOG.info("jobByNameWithProjectId: {}", jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectIdProd));
            Long existingId;
            if (jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectIdProd) == null || jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId).size() == 0) {
                existingId = null;
            } else {
                existingId = jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectIdProd).get(0).getId();
            }
            //Long existingId = jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId);
            if (existingId != null) {
                idCatalog.getSqoop().put(oldId, existingId);
                throw new ExchangisJobServerException(31101, "Already exits duplicated job name(存在重复任务名称) jobName is:" + "[" + sqoop.getJobName() + "]");
            } else {
                //sqoop.setJobName("hahaha");
                LOG.info("Sqoop job content is: {}, Modify user is: {}, jobType is: {}", sqoop.getContent(), sqoop.getExecuteUser(), sqoop.getJobType());
                jobInfoService.createJob(sqoop);
                idCatalog.getSqoop().put(oldId, sqoop.getId());
            }
        }
    }

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
