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
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.OptionElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskSpeedLimitVO;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.manager.label.utils.LabelUtils;
import org.apache.linkis.server.security.SecurityFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@Service
public class ExchangisJobServiceImpl extends ServiceImpl<ExchangisJobMapper, ExchangisJobVO> implements ExchangisJobService {

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
        ExchangisJobVO exchangisJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJobVO.class);
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
    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String type, String name) {
        LambdaQueryChainWrapper<ExchangisJobVO> query =
                exchangisJobService.lambdaQuery().eq(ExchangisJobVO::getProjectId, projectId);
        if (StringUtils.isNotBlank(type)) {
            query.eq(ExchangisJobVO::getJobType, type);
        }
        if (StringUtils.isNotBlank(name)) {
            query.like(ExchangisJobVO::getJobName, name.trim());
        }
        List<ExchangisJobVO> exchangisJobs = query.list();

        Stream<ExchangisJobBasicInfoVO> returnlist = Optional.ofNullable(exchangisJobs).orElse(new ArrayList<>()).stream()
                .map(job -> modelMapper.map(job, ExchangisJobBasicInfoVO.class));
        return returnlist.collect(Collectors.toList());
    }

    @Override
    public List<ExchangisJobBasicInfoVO> getJobListByDssProject(long dssProjectId, String type, String name) {
        LambdaQueryChainWrapper<ExchangisJobVO> query =
                exchangisJobService.lambdaQuery().eq(ExchangisJobVO::getDssProjectId, dssProjectId);
        if (StringUtils.isNotBlank(type)) {
            query.eq(ExchangisJobVO::getJobType, type);
        }
        if (StringUtils.isNotBlank(name)) {
            query.like(ExchangisJobVO::getJobName, name.trim());
        }
        List<ExchangisJobVO> exchangisJobs = query.list();

        Stream<ExchangisJobBasicInfoVO> returnlist = Optional.ofNullable(exchangisJobs).orElse(new ArrayList<>()).stream()
                .map(job -> modelMapper.map(job, ExchangisJobBasicInfoVO.class));
        return returnlist.collect(Collectors.toList());
    }

    @Override
    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId) {
        ExchangisJobVO oldJob = exchangisJobService.getById(sourceJobId);
        ExchangisJobVO newJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJobVO.class);
        newJob.setProjectId(oldJob.getProjectId());
        newJob.setJobType(oldJob.getJobType());
        newJob.setEngineType(oldJob.getEngineType());
        exchangisJobService.save(newJob);
        return modelMapper.map(newJob, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id) {
        ExchangisJobVO job = exchangisJobService.getById(id);
        job.setJobName(exchangisJobBasicInfoDTO.getJobName());
        job.setJobLabels(exchangisJobBasicInfoDTO.getJobLabels());
        job.setJobDesc(exchangisJobBasicInfoDTO.getJobDesc());
        exchangisJobService.updateById(job);

        return modelMapper.map(job, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO updateJobByDss(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, String nodeId) {
        Optional<ExchangisJobVO> optional = this.getByNodeId(nodeId);
        if (optional.isPresent()) {
            ExchangisJobVO job = optional.get();
            job.setJobName(exchangisJobBasicInfoDTO.getJobName());
            job.setJobLabels(exchangisJobBasicInfoDTO.getJobLabels());
            job.setJobDesc(exchangisJobBasicInfoDTO.getJobDesc());
            exchangisJobService.updateById(job);
            return modelMapper.map(job, ExchangisJobBasicInfoVO.class);
        }
        return null;
    }

    private Optional<ExchangisJobVO> getByNodeId(String nodeId) {
        ExchangisJobVO job = null;
        LambdaQueryChainWrapper<ExchangisJobVO> query =
                exchangisJobService.lambdaQuery().eq(ExchangisJobVO::getNodeId, nodeId);
        List<ExchangisJobVO> exchangisJobs = query.list();
        if (null != exchangisJobs && exchangisJobs.size() == 1) {
            job = exchangisJobs.get(0);
        }
        return Optional.ofNullable(job);
    }

    @Override
    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile) {

        return null;
    }

    @Override
    public void deleteJob(Long id) {
        exchangisJobService.removeById(id);
        this.exchangisJobDsBindService.updateJobDsBind(id, new ArrayList<>());
    }

    @Override
    public void deleteJobByDss(String nodeId) {
        this.getByNodeId(nodeId).ifPresent(job -> {
            exchangisJobService.removeById(job.getId());
            exchangisJobDsBindService.updateJobDsBind(job.getId(), new ArrayList<>());
        });
    }

    public ExchangisJobVO getJob(Long id) throws ExchangisJobErrorException {
        return this.getJob(null, id);
    }

    @Override
    public ExchangisJobVO getJob(HttpServletRequest request, Long id) throws ExchangisJobErrorException {
        ExchangisJobVO exchangisJob = exchangisJobService.getById(id);
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
    public ExchangisJobVO getJobByDss(HttpServletRequest request, String nodeId) throws ExchangisJobErrorException {

        Optional<ExchangisJobVO> optional = this.getByNodeId(nodeId);
        if (optional.isPresent()) {
            ExchangisJobVO exchangisJob = optional.get();
            List<ExchangisDataSourceUIViewer> jobDataSourceUIs = exchangisDataSourceService.getJobDataSourceUIs(request, exchangisJob.getId());
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
            return exchangisJob;
        }

      return null;
    }



    @Override
    public ExchangisJobVO updateJobConfig(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobErrorException {
        ExchangisJobVO exchangisJob = exchangisJobService.getById(id);
        exchangisJob.setProxyUser(exchangisJobContentDTO.getProxyUser());
        exchangisJob.setExecuteNode(exchangisJobContentDTO.getExecuteNode());
        exchangisJob.setSyncType(exchangisJobContentDTO.getSyncType());
        exchangisJob.setJobParams(exchangisJobContentDTO.getJobParams());
        exchangisJobService.updateById(exchangisJob);

        return this.getJob(id);
    }

    @Override
    public ExchangisJobVO updateJobContent(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
            throws ExchangisJobErrorException, ExchangisDataSourceException {
        ExchangisJobVO exchangisJob = exchangisJobService.getById(id);
        final String engine = exchangisJob.getEngineType();

        // 校验是否有重复子任务名
        List<ExchangisJobInfoContent> content = LabelUtils.Jackson.fromJson(exchangisJobContentDTO.getContent(), List.class, ExchangisJobInfoContent.class);
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

    @Override
    public List<ElementUI> getSpeedLimitSettings(Long id, String taskName) {
        ExchangisJobVO exchangisJob = exchangisJobService.getById(id);
        Map<String, String> values = new HashMap<>();
        if (null != exchangisJob && null != exchangisJob.getContent() && !"".equals(exchangisJob.getContent())) {
            List<ExchangisJobInfoContent> o = LabelUtils.Jackson.fromJson(exchangisJob.getContent(), List.class, ExchangisJobInfoContent.class);
            o.forEach(task -> {
                if (task.getSubJobName().equals(taskName)) {
                    Optional.ofNullable(task.getSettings()).orElse(new ArrayList<>()).forEach(setting -> {
                        values.put(setting.getConfigKey(), setting.getConfigValue());
                    });
                }
            });
        }

        List<ElementUI> jobEngineSettingsUI = this.exchangisDataSourceService.getJobEngineSettingsUI(exchangisJob.getEngineType());
        jobEngineSettingsUI.forEach(s -> {
            if (values.containsKey(s.getField())) {
                if (s instanceof InputElementUI) {
                    InputElementUI e = (InputElementUI) s;
                    e.setValue(values.get(s.getField()));
                }
                if (s instanceof OptionElementUI) {
                    OptionElementUI e = (OptionElementUI) s;
                    e.setValue(values.get(s.getField()));
                }
            }
        });

        return jobEngineSettingsUI;

    }

    @Override
    public void setSpeedLimitSettings(Long id, String taskName, ExchangisTaskSpeedLimitVO settings) {
        ExchangisJobVO exchangisJob = exchangisJobService.getById(id);
        JsonArray content = new JsonParser().parse(exchangisJob.getContent()).getAsJsonArray();
        JsonArray newSet = new JsonArray();
        settings.getSettings().forEach(s -> {
            JsonObject json = new JsonObject();
            json.addProperty("config_key", s.getConfig_key());
            json.addProperty("config_name", s.getConfig_name());
            json.addProperty("config_value", s.getConfig_value());
            json.addProperty("sort", s.getSort());
            newSet.add(json);
        });
        content.forEach(c -> {
            JsonObject task = c.getAsJsonObject();
            if (task.get("subJobName").getAsString().equals(taskName)) {
                task.remove("settings");
                task.add("settings", newSet);
            }
        });
        exchangisJob.setContent(content.toString());
        exchangisJobService.updateById(exchangisJob);
    }

}
