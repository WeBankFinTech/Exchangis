package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.*;
import com.webank.wedatasphere.exchangis.project.provider.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.provider.exception.ExchangisProjectExceptionCode;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectDsRelationMapper;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectUserMapper;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectDsRelationMapper projectDsRelationMapper;

    @Resource
    private ExchangisJobOpenService jobServiceOpenApi;

    @Autowired
    private ExchangisJobDsBindMapper jobDataSourceBindMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ExchangisDataSourceService dataSourceService;

    @Autowired
    private ExchangisJobEntityDao exchangisJobEntityDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createProject(ExchangisProjectInfo projectInfo, String userName) {
        // Construct the entity
        ExchangisProject project = new ExchangisProject();
        project.setName(projectInfo.getName());
        project.setDescription(projectInfo.getDescription());
        project.setDomain(Optional.ofNullable(projectInfo.getDomain()).orElse(ExchangisProject.Domain.STANDALONE.name()));
        project.setLabels(projectInfo.getLabel());
        project.setSourceMap(projectInfo.getSource());
        project.setViewUsers(projectInfo.getViewUsers());
        project.setEditUsers(projectInfo.getEditUsers());
        project.setExecUsers(projectInfo.getExecUsers());
        project.setCreateUser(userName);
        project.setCreateTime(Calendar.getInstance().getTime());
        this.projectMapper.insertOne(project);

        Long projectId = project.getId();
        List<ExchangisProjectDsRelation> projectDataSources = new ArrayList<>();
        List<ExchangisProjectDsVo> dataSources = projectInfo.getDataSources();
        if (Objects.nonNull(dataSources) && !dataSources.isEmpty()) {
            dataSources.forEach(dataSource -> {
                ExchangisProjectDsRelation exchangisProjectDsRelation = new ExchangisProjectDsRelation(dataSource);
                exchangisProjectDsRelation.setProjectId(projectId);
                projectDataSources.add(exchangisProjectDsRelation);
            });
            this.projectDsRelationMapper.insertBatch(projectDataSources);
        }

        Map<String, ExchangisProjectUser> projectUserMap = new HashMap<>();
        if (Objects.nonNull(project.getViewUsers()) && project.getViewUsers().length() != 0) {
            for (String viewUser : project.getViewUsers().split(",")) {
                ExchangisProjectUser projectUser = new ExchangisProjectUser();
                projectUser.setProjectId(project.getId());
                projectUser.setPrivUser(viewUser);
                projectUser.setPriv(4);
                projectUser.setUpdateTime(project.getLastUpdateTime());
                projectUserMap.put(viewUser ,projectUser);
            }
        }
        if (Objects.nonNull(project.getEditUsers()) && project.getEditUsers().length() != 0) {
            for (String editUser : project.getEditUsers().split(",")) {
                if (Objects.nonNull(projectUserMap.get(editUser))) {
                    projectUserMap.get(editUser).setPriv(6);
                } else {
                    ExchangisProjectUser projectUser = new ExchangisProjectUser();
                    projectUser.setProjectId(project.getId());
                    projectUser.setPrivUser(editUser);
                    projectUser.setPriv(6);
                    projectUser.setUpdateTime(project.getLastUpdateTime());
                    projectUserMap.put(editUser ,projectUser);
                }
            }
        }
        if (Objects.nonNull(project.getExecUsers()) && project.getExecUsers().length() != 0) {
            for (String execUser : project.getExecUsers().split(",")) {
                if (Objects.nonNull(projectUserMap.get(execUser))) {
                    projectUserMap.get(execUser).setPriv(7);
                } else {
                    ExchangisProjectUser projectUser = new ExchangisProjectUser();
                    projectUser.setProjectId(project.getId());
                    projectUser.setPrivUser(execUser);
                    projectUser.setPriv(7);
                    projectUser.setUpdateTime(project.getLastUpdateTime());
                    projectUserMap.put(execUser ,projectUser);
                }
            }
        }

        if(projectUserMap.size() > 0) {
            this.projectUserMapper.addProjectUser(new ArrayList<>(projectUserMap.values()));

        }
        return project.getId();
    }

    @Override
    public boolean existsProject(Long projectId, String projectName) {
        Integer count = this.projectMapper.existsOne(projectId, projectName);
        return null != count && count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(ExchangisProjectInfo projectInfo, String userName) throws ExchangisProjectErrorException {
        Long projectId = Long.valueOf(projectInfo.getId());
        ExchangisProject updatedProject = new ExchangisProject();
        updatedProject.setId(projectId);
        updatedProject.setName(projectInfo.getName());
        updatedProject.setDescription(projectInfo.getDescription());
        updatedProject.setLabels(projectInfo.getLabel());
        updatedProject.setViewUsers(projectInfo.getViewUsers());
        updatedProject.setEditUsers(projectInfo.getEditUsers());
        updatedProject.setExecUsers(projectInfo.getExecUsers());
        updatedProject.setDescription(projectInfo.getDescription());
        // Set the updated properties
        updatedProject.setLastUpdateUser(userName);
        updatedProject.setLastUpdateTime(Calendar.getInstance().getTime());
        this.projectMapper.updateOne(updatedProject);

        List<ExchangisProjectDsRelation> projectDataSources = new ArrayList<>();
        List<ExchangisProjectDsVo> dataSources = projectInfo.getDataSources();
        if (Objects.nonNull(dataSources) && !dataSources.isEmpty()) {
            // check delete
            List<ExchangisProjectDsRelation> projectDsRelations = this.projectDsRelationMapper.listByProject(projectId, null);

            Set<String> dsNames = dataSources.stream()
                    .map(ExchangisProjectDsVo::getName)
                    .collect(Collectors.toSet());
            Set<String> deleteNames = projectDsRelations.stream()
                    .map(ExchangisProjectDsRelation::getDsName)
                    .filter(name -> !dsNames.contains(name))
                    .collect(Collectors.toSet());

            List<ExchangisJobEntity> detailList = exchangisJobEntityDao.getDetailList(projectId);
            for (ExchangisJobEntity jobDetail : detailList) {
                List<ExchangisJobInfoContent> contentList = JobUtils.parseJobContent(jobDetail.getJobContent());
                for (ExchangisJobInfoContent content : contentList) {
                    String sourceName = content.getDataSources().getSource().getName();
                    String sinkName = content.getDataSources().getSink().getName();
                    if (deleteNames.contains(sourceName) || deleteNames.contains(sinkName)) {
                        throw new ExchangisProjectErrorException(ExchangisProjectExceptionCode.RELEASE_PROJECT_DS_RELATION_ERROR.getCode(),
                                "Release project ds relation error, the delete datasource is bound by the job " + jobDetail.getName() + " with subJob " + content.getSubJobName()
                                + " （解绑的数据源已存在任务配置中，请在任务中解绑后重试）");
                    }
                }
            }

            // first to delete then to insert
            this.projectDsRelationMapper.deleteByProject(projectId);
            dataSources.forEach(dataSource -> {
                ExchangisProjectDsRelation exchangisProjectDsRelation = new ExchangisProjectDsRelation(dataSource);
                exchangisProjectDsRelation.setProjectId(projectId);
                projectDataSources.add(exchangisProjectDsRelation);
            });
            this.projectDsRelationMapper.insertBatch(projectDataSources);
        }

        Map<String, ExchangisProjectUser> projectUserMap = new HashMap<>();
        if (Objects.nonNull(updatedProject.getViewUsers()) && updatedProject.getViewUsers().length() != 0) {
            for (String viewUser : updatedProject.getViewUsers().split(",")) {
                ExchangisProjectUser projectUser = new ExchangisProjectUser();
                projectUser.setProjectId(updatedProject.getId());
                projectUser.setPrivUser(viewUser);
                projectUser.setPriv(4);
                projectUser.setUpdateTime(updatedProject.getLastUpdateTime());
                projectUserMap.put(viewUser ,projectUser);
            }
        }
        if (Objects.nonNull(updatedProject.getEditUsers()) && updatedProject.getEditUsers().length() != 0) {
            for (String editUser : updatedProject.getEditUsers().split(",")) {
                if (Objects.nonNull(projectUserMap.get(editUser))) {
                    projectUserMap.get(editUser).setPriv(6);
                } else {
                    ExchangisProjectUser projectUser = new ExchangisProjectUser();
                    projectUser.setProjectId(updatedProject.getId());
                    projectUser.setPrivUser(editUser);
                    projectUser.setPriv(6);
                    projectUser.setUpdateTime(updatedProject.getLastUpdateTime());
                    projectUserMap.put(editUser ,projectUser);
                }
            }
        }
        if (Objects.nonNull(updatedProject.getExecUsers()) && updatedProject.getExecUsers().length() != 0) {
            for (String execUser : updatedProject.getExecUsers().split(",")) {
                if (Objects.nonNull(projectUserMap.get(execUser))) {
                    projectUserMap.get(execUser).setPriv(7);
                } else {
                    ExchangisProjectUser projectUser = new ExchangisProjectUser();
                    projectUser.setProjectId(updatedProject.getId());
                    projectUser.setPrivUser(execUser);
                    projectUser.setPriv(7);
                    projectUser.setUpdateTime(updatedProject.getLastUpdateTime());
                    projectUserMap.put(execUser ,projectUser);
                }
            }
        }

        this.projectUserMapper.deleteProjectUser(Long.valueOf(projectInfo.getId()));
        if(projectUserMap.size() > 0) {
            this.projectUserMapper.addProjectUser(new ArrayList<>(projectUserMap.values()));
        }
    }

    @Override
    public PageResult<ExchangisProjectInfo> queryProjects(ProjectQueryVo queryVo) {
        PageHelper.startPage(queryVo.getPage(), queryVo.getPageSize());
        try{
            // Admin user get projects
            List<ExchangisProject> projects = GlobalConfiguration.isAdminUser(queryVo.getCreateUser())?
                    this.projectMapper.queryPageInAll(queryVo) :
                    this.projectMapper.queryPageList(queryVo);
            PageInfo<ExchangisProject> pageInfo = new PageInfo<>(projects);
            // query project datasource relation
            List<Long> ids = projects.stream().map(ExchangisProject::getId).collect(Collectors.toList());
            List<ExchangisProjectDsRelation> proDsRelations = new ArrayList<>();
            if (!ids.isEmpty()) {
                proDsRelations = projectDsRelationMapper.listByProjects(ids);
            }

            List<ExchangisProjectInfo> infoList = new ArrayList<>();
            for (ExchangisProject project : projects) {
                ExchangisProjectInfo exchangisProjectInfo = new ExchangisProjectInfo(project);
                if (!proDsRelations.isEmpty()) {
                    exchangisProjectInfo.setDataSources(
                            proDsRelations.stream().filter(item -> item.getProjectId().equals(project.getId())).map(ExchangisProjectDsVo::new).collect(Collectors.toList())
                    );
                }
                infoList.add(exchangisProjectInfo);
            }

            PageResult<ExchangisProjectInfo> pageResult = new PageResult<>();
            pageResult.setList(infoList);
            pageResult.setTotal(pageInfo.getTotal());
            return pageResult;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long projectId) throws ExchangisJobException {
        // First to delete the project to lock the record
        this.projectMapper.deleteOne(projectId);
        // Query the related job
        ExchangisJobQueryVo queryVo = new ExchangisJobQueryVo();
        queryVo.setProjectId(projectId);
        List<ExchangisJobEntity> jobEntities = this.jobServiceOpenApi.queryJobs(queryVo, false);
        if (!jobEntities.isEmpty()){
            List<Long> ids = jobEntities.stream().map(ExchangisJobEntity::getId).collect(Collectors.toList());
            this.jobServiceOpenApi.deleteJobBatch(ids);
            QueryWrapper<ExchangisJobDsBind> dsBindQuery = new QueryWrapper<ExchangisJobDsBind>().in("job_id", ids);
            this.jobDataSourceBindMapper.delete(dsBindQuery);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectByName(String name) throws ExchangisJobException {
        // First to delete the project to lock the record
        ExchangisProject project = this.projectMapper.selectByName(name);
        this.projectMapper.deleteByName(name);
    }
    @Override
    public ExchangisProjectInfo getProjectDetailById(Long projectId) {
        ExchangisProject project = this.projectMapper.getDetailById(projectId);
        if (Objects.nonNull(project)){
            ExchangisProjectInfo projectVo = getProject(project);
            projectVo.setViewUsers(project.getViewUsers());
            projectVo.setEditUsers(project.getEditUsers());
            projectVo.setExecUsers(project.getExecUsers());
            projectVo.setSource(project.getSourceMap());
            List<ExchangisProjectDsRelation> proDsRelations = projectDsRelationMapper
                    .queryPageList(new ProjectDsQueryVo(projectId));
            projectVo.setDataSources(proDsRelations.stream()
                    .map(ExchangisProjectDsVo::new).collect(Collectors.toList()));
            return projectVo;
        }
        return null;
    }

    /**
     * Basic info query
     * @param projectId project id
     * @return project info
     */
    @Override
    public ExchangisProjectInfo getProjectById(Long projectId) {
        ExchangisProject project = projectMapper.getBasicById(projectId);
        if (Objects.nonNull(project)){
            return getProject(project);
        }
        return null;
    }

    @Override
    public ExchangisProjectInfo selectByName(String name){
        ExchangisProject project = this.projectMapper.selectByName(name);
        if (Objects.nonNull(project)){
            return getProject(project);
        }
        return null;
    }

    private ExchangisProjectInfo getProject(ExchangisProject project) {
        ExchangisProjectInfo exchangisProjectInfo = new ExchangisProjectInfo(project);
        //todo add datasource 在sql中获取数据源信息即可
        exchangisProjectInfo.setDataSources(null);
        return exchangisProjectInfo;
    }

    @Override
    public ExchangisProjectUser queryProjectUser(ExchangisProjectUserVo exchangisProjectUserVo) {
        ExchangisProjectUser projectUser = new ExchangisProjectUser(Long.valueOf(exchangisProjectUserVo.getProjectId()), exchangisProjectUserVo.getPrivUser());
        return this.projectUserMapper.queryProjectUser(projectUser);
    }
}
