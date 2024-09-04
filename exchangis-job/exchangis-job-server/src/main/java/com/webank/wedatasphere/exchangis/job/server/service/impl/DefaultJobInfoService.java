package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceUIGetter;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.server.validator.JobValidateResult;
import com.webank.wedatasphere.exchangis.job.server.validator.JobValidator;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.manager.label.utils.LabelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.VALIDATE_JOB_ERROR;

/**
 * Default implement
 */
@Service
public class DefaultJobInfoService implements JobInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJobInfoService.class);

    @Autowired
    private ExchangisJobDsBindServiceImpl exchangisJobDsBindService;

    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private ExchangisJobEntityDao jobEntityDao;

    @Resource
    private DataSourceUIGetter uiGetter;

    /**
     * Project open service
     */
    @Resource
    private ProjectOpenService projectOpenService;
    /**
     * Validators
     */
    @Resource
    private List<JobValidator<?>> validators = new ArrayList<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo createJob(ExchangisJobVo jobVo) {
        ExchangisJobEntity jobEntity = new ExchangisJobEntity();
        jobEntity.setProjectId(Long.parseLong(jobVo.getProjectId()));
        jobEntity.setJobType(jobVo.getJobType());
        jobEntity.setEngineType(jobVo.getEngineType());
        jobEntity.setJobLabel(jobVo.getJobLabels());
        jobEntity.setJobLabels(jobVo.getJobLabels());
        jobEntity.setName(jobVo.getJobName());
        jobEntity.setJobDesc(jobVo.getJobDesc());
        jobEntity.setExecuteUser(jobVo.getProxyUser());
        jobEntity.setJobParams(jobVo.getJobParams());
        jobEntity.setCreateUser(jobVo.getCreateUser());
        jobEntity.setCreateTime(Calendar.getInstance().getTime());
        jobEntity.setSource(Json.toJson(jobVo.getSource(), null));
        //jobEntity.setJobContent(jobVo.getContent());
        jobEntity.setModifyUser(jobVo.getModifyUser());
        //Map<String, Object> contentVo = BDPJettyServerHelper.gson().fromJson(jobVo.getContent(), Map.class);
        if(jobVo.getContent() != null) {
            jobEntity.setJobContent(jobVo.getContent());
        }
        jobEntityDao.addJobEntity(jobEntity);
        jobVo.setId(jobEntity.getId());
        jobVo.setCreateTime(jobEntity.getCreateTime());
        LOG.info("Success to create job: {}", jobEntity.toString());
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
        jobEntity.setJobLabels(jobVo.getJobLabels());
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
        if (Objects.isNull(exchangisJob)) {
            return null;
        }
        ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
        jobVo.setProjectId(String.valueOf(exchangisJob.getProjectId()));
        if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
            jobVo.setContent(exchangisJob.getJobContent());
            jobVo.setSource(Objects.nonNull(exchangisJob.getSource())?
                    Json.fromJson(exchangisJob.getSource(), Map.class, String.class, Object.class) : new HashMap<>());
        }
        return jobVo;
    }

    @Override
    public  List<ExchangisJobVo> getByNameAndProjectId(String jobName, Long projectId) {
        List<ExchangisJobEntity> exchangisJobs = this.jobEntityDao.getByNameAndProjectId(jobName, projectId);
        List<ExchangisJobVo> exchangisJobVos = new ArrayList<>();
        for(ExchangisJobEntity exchangisJob : exchangisJobs){
            ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
            if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
                jobVo.setContent(exchangisJob.getJobContent());
                jobVo.setSource(Objects.nonNull(exchangisJob.getSource())?
                        Json.fromJson(exchangisJob.getSource(), Map.class, String.class, Object.class) : new HashMap<>());
            }
            exchangisJobVos.add(jobVo);
        }

        return exchangisJobVos;
    }

    @Override
    public  List<ExchangisJobVo> getByNameWithProjectId(String jobName, Long projectId) {
        List<ExchangisJobEntity> exchangisJobs = this.jobEntityDao.getByNameWithProjectId(jobName, projectId);
        List<ExchangisJobVo> exchangisJobVos = new ArrayList<>();
        for(ExchangisJobEntity exchangisJob : exchangisJobs){
            ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
            if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
                jobVo.setContent(exchangisJob.getJobContent());
                jobVo.setSource(Objects.nonNull(exchangisJob.getSource())?
                        Json.fromJson(exchangisJob.getSource(), Map.class, String.class, Object.class) : new HashMap<>());
            }
            exchangisJobVos.add(jobVo);
        }

        return exchangisJobVos;
    }

    @Override
    public ExchangisJobVo getDecoratedJob(HttpServletRequest request, Long id) throws ExchangisJobServerException {
        ExchangisJobEntity exchangisJob = this.jobEntityDao.getDetail(id);
        ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
        if (exchangisJob != null && StringUtils.isNotBlank(exchangisJob.getJobContent())) {
            // Rebuild the job content with ui configuration
            List<ExchangisDataSourceUIViewer> jobDataSourceUIs = this.uiGetter.getJobDataSourceUIs(request, id);
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
    public List<ExchangisJobVo> getSubJobList(HttpServletRequest request, Long projectId)  throws ExchangisJobServerException{
        List<ExchangisJobEntity> exchangisJobList = this.jobEntityDao.getDetailList(projectId);
        List<ExchangisJobVo> exchangisJobVos = new ArrayList<>();
        if(!exchangisJobList.isEmpty()) {
            for(ExchangisJobEntity exchangisJob : exchangisJobList){
                ExchangisJobVo jobVo = new ExchangisJobVo(exchangisJob);
                if(StringUtils.isNotBlank(exchangisJob.getJobContent())){
                    List<ExchangisDataSourceUIViewer> jobDataSourceUIs = this.uiGetter.getJobDataSourceUIs(request, exchangisJob.getId());
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
                exchangisJobVos.add(jobVo);
            }
        }
        return exchangisJobVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo updateJobConfig(ExchangisJobVo jobVo) {
        ExchangisJobEntity jobEntity = this.jobEntityDao.getBasicInfo(jobVo.getId());
        Map<String, Object> sourceMap = StringUtils.isNotBlank(jobEntity.getSource())?
                Json.fromJson(jobEntity.getSource(), Map.class, String.class, Object.class) : null;
        jobEntity.setExecuteUser(jobVo.getProxyUser());
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
    public ExchangisJobVo updateJobContent(String requestUser, ExchangisJobVo jobVo) throws ExchangisJobServerException, ExchangisDataSourceException {
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
            String sourceType = task.getDataSources().getSource().getType();
            String sinkType = task.getDataSources().getSink().getType();
            this.dataSourceService.supportDataSource(engine, sourceType, sinkType);
            ExchangisJobDsBind dsBind = new ExchangisJobDsBind();
            dsBind.setJobId(jobVo.getId());
            dsBind.setTaskIndex(i);
            dsBind.setSourceDsId(task.getDataSources().getSource().getId());
            dsBind.setSinkDsId(task.getDataSources().getSink().getId());
            dsBinds.add(dsBind);
        }
        for(JobValidator<?> validator : this.validators){
            JobValidateResult<?> result = validator.doValidate(jobVo.getJobName(), content, null);
            if (GlobalConfiguration.isAdminUser(requestUser)){
                continue;
            }
            if (Objects.nonNull(result) && !result.isResult()){
                //Just throw exception
                throw new ExchangisJobServerException(VALIDATE_JOB_ERROR.getCode(), result.getMessage());
            }
        }
        exchangisJob.setModifyUser(jobVo.getModifyUser());
        exchangisJob.setLastUpdateTime(jobVo.getModifyTime());
        this.exchangisJobDsBindService.updateJobDsBind(jobId, dsBinds);
        this.jobEntityDao.upgradeContent(exchangisJob);
        relateProjectDsFromJob(exchangisJob, content);
        return jobVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangisJobVo copyJob(ExchangisJobVo jobVo) {
        ExchangisJobVo job = this.getJob(jobVo.getId(), false);
        ExchangisJobVo newJob = this.createJob(job);
        return newJob;
    }

    /**
     * Relate project and data source from job content
     * @param exchangisJob job info
     * @param contents job content list
     */
    private void relateProjectDsFromJob(ExchangisJobEntity exchangisJob, List<ExchangisJobInfoContent> contents){
        Long projectId = exchangisJob.getProjectId();
        if (Objects.nonNull(projectId)){
            ExchangisProjectInfo project =  this.projectOpenService.getProject(projectId);
            if (Objects.nonNull(project) && ! ExchangisProject.Domain.DSS.toString().equals(project.getDomain())){
                Map<String, ExchangisProjectDsRelation> relations = new HashMap<>();
                Calendar cal = Calendar.getInstance();
                for (ExchangisJobInfoContent content :  contents){
                    ExchangisJobDataSourcesContent.ExchangisJobDataSource[]
                            dataSources = new ExchangisJobDataSourcesContent.ExchangisJobDataSource[]{
                                    content.getDataSources().getSource(), content.getDataSources().getSink()};
                    for (ExchangisJobDataSourcesContent.ExchangisJobDataSource jobDataSource : dataSources){
                        if (StringUtils.isNotBlank(jobDataSource.getName())) {
                            ExchangisProjectDsRelation relation =
                                    new ExchangisProjectDsRelation();
                            relation.setDsName(jobDataSource.getName());
                            relation.setDsId(jobDataSource.getId());
                            relation.setDsType(jobDataSource.getType());
                            relation.setDsCreator(jobDataSource.getCreator());
                            relation.setLastUpdateTime(cal.getTime());
                            relations.put(jobDataSource.getName(), relation);
                        }
                    }
                }
                if (!relations.isEmpty()) {
                    this.projectOpenService.addDsRelations(new ArrayList<>(relations.values()));
                }
            }
        }
    }
}
