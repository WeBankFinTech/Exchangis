package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.manager.label.utils.LabelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implement
 */
@Service
public class DefaultJobInfoService implements JobInfoService {

    @Autowired
    private ExchangisJobDsBindServiceImpl exchangisJobDsBindService;

    @Autowired
    private ExchangisDataSourceService exchangisDataSourceService;

    @Resource
    private ExchangisJobEntityDao jobEntityDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo createJob(ExchangisJobVo jobVo) {
        // Define the entity
        ExchangisJobEntity jobEntity = new ExchangisJobEntity();
        jobEntity.setProjectId(jobVo.getProjectId());
        jobEntity.setJobType(jobVo.getJobType());
        jobEntity.setEngineType(jobVo.getEngineType());
        jobEntity.setJobLabels(jobVo.getJobLabels());
        jobEntity.setName(jobVo.getJobName());
        jobEntity.setJobDesc(jobVo.getJobDesc());
        jobEntity.setExecuteUser(jobVo.getExecuteUser());
        jobEntity.setJobParams(jobVo.getJobParams());
        jobEntity.setCreateUser(jobVo.getCreateUser());
        jobEntity.setCreateTime(Calendar.getInstance().getTime());
        jobEntity.setSource(Json.toJson(jobVo.getSource(), null));
        jobEntityDao.addJobEntity(jobEntity);
        jobVo.setId(jobEntity.getId());
        jobVo.setCreateTime(jobEntity.getCreateTime());
        return jobVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo updateJob(ExchangisJobVo jobVo) {
        ExchangisJobEntity jobEntity = new ExchangisJobEntity();
        jobEntity.setId(jobVo.getId());
        jobEntity.setJobType(jobVo.getJobType());
        jobEntity.setEngineType(jobVo.getEngineType());
        jobEntity.setJobLabel(jobVo.getJobLabels());
        jobEntity.setName(jobVo.getJobName());
        jobEntity.setJobDesc(jobVo.getJobDesc());
        jobEntity.setLastUpdateTime(Calendar.getInstance().getTime());
        jobEntity.setModifyUser(jobVo.getModifyUser());
        jobEntityDao.upgradeBasicInfo(jobEntity);
        return jobVo;
    }

    @Override
    public PageResult<ExchangisJobVo> queryJobList(ExchangisJobQueryVo queryVo){
        PageHelper.startPage(queryVo.getPage(), queryVo.getPageSize());
        try{
            List<ExchangisJobEntity> jobEntities = this.jobEntityDao.queryPageList(queryVo);
            PageInfo<ExchangisJobEntity> pageInfo = new PageInfo<>(jobEntities);
            List<ExchangisJobVo> infoList = jobEntities
                    .stream().map(ExchangisJobVo::new).collect(Collectors.toList());
            PageResult<ExchangisJobVo> pageResult = new PageResult<>();
            pageResult.setList(infoList);
            pageResult.setTotal(pageInfo.getTotal());
            return pageResult;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id) {
        this.jobEntityDao.deleteJobEntity(id);
        this.exchangisJobDsBindService.updateJobDsBind(id, new ArrayList<>());
    }

    @Override
    public ExchangisJobVo getJob(Long id, boolean basic) {
        ExchangisJobEntity exchangisJob = basic ? this.jobEntityDao.getBasicInfo(id) : this.jobEntityDao.getDetail(id);
        ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
        if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
            jobVo.setContent(exchangisJob.getJobContent());
            jobVo.setSource(Objects.nonNull(exchangisJob.getSource())?
                    Json.fromJson(exchangisJob.getSource(), Map.class, String.class, Object.class) : new HashMap<>());
        }
        return jobVo;
    }

    @Override
    public ExchangisJobVo getDecoratedJob(HttpServletRequest request, Long id) throws ExchangisJobServerException {
        ExchangisJobEntity exchangisJob = this.jobEntityDao.getDetail(id);
        ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
        if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
            // Rebuild the job content with ui configuration
            List<ExchangisDataSourceUIViewer> jobDataSourceUIs = exchangisDataSourceService.getJobDataSourceUIs(request, id);
            ObjectMapper objectMapper = JsonUtils.jackson();
            try {
                String content = objectMapper.writeValueAsString(jobDataSourceUIs);
                JsonNode contentJsonNode = objectMapper.readTree(content);
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.set("subJobs", contentJsonNode);
                jobVo.setContent(objectNode.toString());
                jobVo.setSource(Objects.nonNull(exchangisJob.getSource())?
                        Json.fromJson(exchangisJob.getSource(), Map.class, String.class, Object.class) : new HashMap<>());
            } catch (JsonProcessingException e) {
                throw new ExchangisJobServerException(31100,
                        "Fail to rebuild the job content with ui (渲染任务内容失败)", e);
            }
        }
        return jobVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo updateJobConfig(ExchangisJobVo jobVo) {
        ExchangisJobEntity jobEntity = this.jobEntityDao.getBasicInfo(jobVo.getId());
        Map<String, Object> sourceMap = StringUtils.isNotBlank(jobEntity.getSource())?
                Json.fromJson(jobEntity.getSource(), Map.class, String.class, Object.class) : null;
        jobEntity.setExecuteUser(jobVo.getExecuteUser());
        jobEntity.setJobParams(jobVo.getJobParams());
        if (Objects.isNull(sourceMap)){
            sourceMap = new HashMap<>();
        }
        sourceMap.putAll(jobVo.getSource());
        jobEntity.setSource(Json.toJson(sourceMap, null));
        jobEntity.setModifyUser(jobVo.getModifyUser());
        jobEntity.setLastUpdateTime(Calendar.getInstance().getTime());
        this.jobEntityDao.upgradeConfig(jobEntity);
        return jobVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo updateJobContent(ExchangisJobVo jobVo) throws ExchangisJobServerException, ExchangisDataSourceException {
        Long jobId = jobVo.getId();
        ExchangisJobEntity exchangisJob = this.jobEntityDao.getDetail(jobId);
        exchangisJob.setJobContent(jobVo.getContent());
        final String engine = exchangisJob.getEngineType();
        // 校验是否有重复子任务名
        List<ExchangisJobInfoContent> content = LabelUtils.Jackson.fromJson(exchangisJob.getJobContent(),
                List.class, ExchangisJobInfoContent.class);
        long count = content.stream().map(ExchangisJobInfoContent::getSubJobName).distinct().count();
        if (count < content.size()) {
            throw new ExchangisJobServerException(31101, "Already exits duplicated job name(存在重复子任务名)");
        }
        List<ExchangisJobDsBind> dsBinds = new ArrayList<>(content.size());
        // 校验引擎是否支持该数据通道
        for (int i = 0; i < content.size(); i++) {
            ExchangisJobInfoContent task = content.get(i);
            String sourceType = task.getDataSources().getSourceId().split("\\.")[0];
            String sinkType = task.getDataSources().getSinkId().split("\\.")[0];
            this.exchangisDataSourceService.checkDSSupportDegree(engine, sourceType, sinkType);
            ExchangisJobDsBind dsBind = new ExchangisJobDsBind();
            dsBind.setJobId(jobVo.getId());
            dsBind.setTaskIndex(i);
            dsBind.setSourceDsId(Long.parseLong(task.getDataSources().getSourceId().split("\\.")[1]));
            dsBind.setSinkDsId(Long.parseLong(task.getDataSources().getSinkId().split("\\.")[1]));
            dsBinds.add(dsBind);
        }
        exchangisJob.setModifyUser(jobVo.getModifyUser());
        exchangisJob.setLastUpdateTime(jobVo.getModifyTime());
        this.exchangisJobDsBindService.updateJobDsBind(jobId, dsBinds);
        this.jobEntityDao.upgradeContent(exchangisJob);
        return jobVo;
    }

}
