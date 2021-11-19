package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.OptionElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskSpeedLimitVO;
import com.webank.wedatasphere.linkis.common.utils.JsonUtils;
import com.webank.wedatasphere.linkis.manager.label.utils.LabelUtils.Jackson;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@Service
public class ExchangisJobServiceImpl extends ServiceImpl<ExchangisJobMapper, ExchangisJob> implements ExchangisJobService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExchangisJobService exchangisJobService;

    @Autowired
    private ExchangisJobDsBindServiceImpl exchangisJobDsBindService;

    @Autowired
    private ExchangisDataSourceService exchangisDataSourceService;

    @Override
    public ExchangisJobBasicInfoVO createJob(HttpServletRequest request, ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJob exchangisJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJob.class);
        String proxyUser = "";
        try {
            proxyUser = SecurityFilter.getLoginUsername(request);
        } catch (Exception e) {
            log.error("Get proxy user error.", e);
        }
        exchangisJob.setProxyUser(proxyUser);
        exchangisJobService.save(exchangisJob);
        return modelMapper.map(exchangisJob, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String jobType, String name) {
        LambdaQueryChainWrapper<ExchangisJob> query =
                exchangisJobService.lambdaQuery().eq(ExchangisJob::getProjectId, projectId);
        if (StringUtils.isNotBlank(jobType)) {
            query.eq(ExchangisJob::getJobType, jobType);
        }
        if (StringUtils.isNotBlank(name)) {
            query.like(ExchangisJob::getJobName, name.trim());
        }
        List<ExchangisJob> exchangisJobs = query.list();

        List<ExchangisJobBasicInfoVO> returnlist = new ArrayList<>();
        exchangisJobs.stream().forEach(job -> returnlist.add(modelMapper.map(job, ExchangisJobBasicInfoVO.class)));

        return returnlist;
    }

    @Override
    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId) {
        ExchangisJob oldJob = exchangisJobService.getById(sourceJobId);
        ExchangisJob newJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJob.class);
        newJob.setProjectId(oldJob.getProjectId());
        newJob.setJobType(oldJob.getJobType());
        newJob.setEngineType(oldJob.getEngineType());
        exchangisJobService.save(newJob);
        return modelMapper.map(newJob, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id) {
        ExchangisJob job = exchangisJobService.getById(id);
        job.setJobName(exchangisJobBasicInfoDTO.getJobName());
        job.setJobLabels(exchangisJobBasicInfoDTO.getJobLabels());
        job.setJobDesc(exchangisJobBasicInfoDTO.getJobDesc());
        exchangisJobService.updateById(job);

        return modelMapper.map(job, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile) {

        return null;
    }

    @Override
    public void deleteJob(Long id) {
        exchangisJobService.removeById(id);
    }

    public ExchangisJob getJob(Long id) throws ExchangisJobErrorException {
        return this.getJob(null, id);
    }

    @Override
    public ExchangisJob getJob(HttpServletRequest request, Long id) throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.getById(id);
        if (exchangisJob != null) {
            // generate subjobs ui content
            List<ExchangisDataSourceUIViewer> jobDataSourceUIs = exchangisDataSourceService.getJobDataSourceUIs(request, id);
            ObjectMapper objectMapper = JsonUtils.jackson();
            try {
                String content = objectMapper.writeValueAsString(jobDataSourceUIs);
                JsonNode contentJsonNode = objectMapper.readTree(content);
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.set("subJobs", contentJsonNode);
                exchangisJob.setContent(objectNode.toString());
            } catch (JsonProcessingException e) {
                throw new ExchangisJobErrorException(31100, "exchangis.subjob.ui.create.error", e);
            }
        }
        return exchangisJob;
    }

    @Override
    public ExchangisJob updateJobConfig(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.getById(id);
        exchangisJob.setProxyUser(exchangisJobContentDTO.getProxyUser());
        exchangisJob.setExecuteNode(exchangisJobContentDTO.getExecuteNode());
        exchangisJob.setSyncType(exchangisJobContentDTO.getSyncType());
        exchangisJob.setJobParams(exchangisJobContentDTO.getJobParams());
        exchangisJobService.updateById(exchangisJob);

        return this.getJob(id);
    }

    @Override
    public ExchangisJob updateJobContent(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobErrorException, ExchangisDataSourceException {
        ExchangisJob exchangisJob = exchangisJobService.getById(id);
        final String engine = exchangisJob.getEngineType();

        // 校验是否有重复子任务名
        List<ExchangisJobInfoContent> content = Jackson.fromJson(exchangisJobContentDTO.getContent(), List.class, ExchangisJobInfoContent.class);
        long count = content.stream().map(ExchangisJobInfoContent::getSubJobName).distinct().count();
        if (count < content.size()) {
            throw new ExchangisJobErrorException(31101, "存在重复子任务名");
        }

        List<ExchangisJobDsBind> dsBinds = new ArrayList<>(content.size());

        // 校验引擎是否支持该数据通道

        for (int i = 0; i < content.size(); i++) {
            ExchangisJobInfoContent task = content.get(i);
            String sourceType = task.getDataSources().getSourceId().split("\\.")[0];
            String sinkType = task.getDataSources().getSinkId().split("\\.")[0];
            this.exchangisDataSourceService.checkDSSupportDegree(engine, sourceType, sinkType);
            ExchangisJobDsBind dsBind = new ExchangisJobDsBind();
            dsBind.setJobId(id);
            dsBind.setTaskIndex(i);
            dsBind.setSourceDsId(Long.parseLong(task.getDataSources().getSourceId().split("\\.")[1]));
            dsBind.setSinkDsId(Long.parseLong(task.getDataSources().getSinkId().split("\\.")[1]));
            dsBinds.add(dsBind);
        }

        this.exchangisJobDsBindService.updateJobDsBind(id, dsBinds);

        exchangisJob.setContent(exchangisJobContentDTO.getContent());
        exchangisJobService.updateById(exchangisJob);
        return this.getJob(id);
    }

//    @Override
//    public List<ElementUI> getSpeedLimitSettings(Long id, String taskName) {
//        ExchangisJob exchangisJob = exchangisJobService.getById(id);
//        Map<String, String> values = new HashMap<>();
//        if (null != exchangisJob && null != exchangisJob.getContent() && !"".equals(exchangisJob.getContent())) {
//            List<ExchangisJobInfoContent> o = Jackson.fromJson(exchangisJob.getContent(), List.class, ExchangisJobInfoContent.class);
//            o.forEach(task -> {
//                if (task.getSubJobName().equals(taskName)) {
//                    Optional.ofNullable(task.getSettings()).orElse(new ArrayList<>()).forEach(setting -> {
//                        values.put(setting.getConfigKey(), setting.getConfigValue());
//                    });
//                }
//            });
//        }
//
//        List<ElementUI> jobEngineSettingsUI = this.exchangisDataSourceService.getJobEngineSettingsUI(exchangisJob.getEngineType());
//        jobEngineSettingsUI.forEach(s -> {
//            if (values.containsKey(s.getField())) {
//                if (s instanceof InputElementUI) {
//                    InputElementUI e = (InputElementUI) s;
//                    e.setValue(values.get(s.getField()));
//                }
//                if (s instanceof OptionElementUI) {
//                    OptionElementUI e = (OptionElementUI) s;
//                    e.setValue(values.get(s.getField()));
//                }
//            }
//        });
//
//        return jobEngineSettingsUI;
//
//    }

//    @Override
//    public void setSpeedLimitSettings(Long id, String taskName, ExchangisTaskSpeedLimitVO settings) {
//        ExchangisJob exchangisJob = exchangisJobService.getById(id);
//        JsonArray content = new JsonParser().parse(exchangisJob.getContent()).getAsJsonArray();
//        JsonArray newSet = new JsonArray();
//        settings.getSettings().forEach(s -> {
//            JsonObject json = new JsonObject();
//            json.addProperty("config_key", s.getConfig_key());
//            json.addProperty("config_name", s.getConfig_name());
//            json.addProperty("config_value", s.getConfig_value());
//            json.addProperty("sort", s.getSort());
//            newSet.add(json);
//        });
//        content.forEach(c -> {
//            JsonObject task = c.getAsJsonObject();
//            if (task.get("subJobName").getAsString().equals(taskName)) {
//                task.remove("settings");
//                task.add("settings", newSet);
//            }
//        });
//        exchangisJob.setContent(content.toString());
//        exchangisJobService.updateById(exchangisJob);
//    }

}
