package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.dto.ExportedProject;
import com.webank.wedatasphere.exchangis.job.server.dto.IdCatalog;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.service.IProjectImportService;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
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

    @Override
    public Message importProject(HttpServletRequest req, Map<String, String> params) throws ExchangisJobServerException, ServerException {
        String userName = SecurityFilter.getLoginUsername(req);
        String resourceId = params.get("resourceId");
        String version = params.get("version");
        Long projectId = Long.parseLong(params.get("projectId"));
        String projectVersion = params.get("projectVersion");
        String flowVersion = params.get("flowVersion");
        String versionSuffix = projectVersion + "_" + flowVersion;
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
            IdCatalog idCatalog = importOpt(projectJson, projectId, versionSuffix);
            message = Message.ok("import Job ok")
                    .data("sqoop", idCatalog.getSqoop())
                    .data("datax", idCatalog.getDatax());

            return message;
        } catch (IOException e) {
            LOG.error("Error occur while import option: {}", projectId);
        }
        finally {
            IOUtils.closeQuietly(bmlDownloadResponse.inputStream());
        }

        return message;
    }

    @Override
    public IdCatalog importOpt(String projectJson, Long projectId, String versionSuffix){
        ExportedProject exportedProject = BDPJettyServerHelper.gson().fromJson(projectJson, ExportedProject.class);
        IdCatalog idCatalog = new IdCatalog();

        importSqoop(projectId, versionSuffix, exportedProject, idCatalog);

        importDatax(projectId, versionSuffix, exportedProject, idCatalog);

        return idCatalog;
    }

    private void importSqoop(Long projectId, String versionSuffix, ExportedProject exportedProject, IdCatalog idCatalog) {
        List<ExchangisJobVo> sqoops = exportedProject.getSqoops();
        if (sqoops == null) {
            return;
        }
        for (ExchangisJobVo sqoop : sqoops) {
            Long oldId = sqoop.getId();
            sqoop.setProjectId(projectId);
            sqoop.setJobName(updateName(sqoop.getJobName(), versionSuffix));
            //Long existingId = (long) 55;
            LOG.info("oldId: {}, projectid: {}, jobName: {}", sqoop.getId(), sqoop.getProjectId(), sqoop.getJobName());
            LOG.info("jobByNameWithProjectId: {}", jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId));
            Long existingId;
            if(jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId) == null || jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId).size() == 0){
                existingId = null;
            }
            else {
                existingId = jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId).get(0).getId();
            }
            //Long existingId = jobInfoService.getByNameWithProjectId(sqoop.getJobName(), projectId);
            if (existingId != null) {
                idCatalog.getSqoop().put(oldId, existingId);
            } else {
                sqoop.setJobName("hahaha");
                jobInfoService.createJob(sqoop);
                idCatalog.getSqoop().put(oldId, sqoop.getId());
            }
        }
    }

    private void importDatax(Long projectId, String versionSuffix, ExportedProject exportedProject, IdCatalog idCatalog) {
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
