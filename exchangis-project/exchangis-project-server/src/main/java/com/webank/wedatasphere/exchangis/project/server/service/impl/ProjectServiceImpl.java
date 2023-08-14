package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectUserMapper;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectUserVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;
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

    @Resource
    private ExchangisJobOpenService jobServiceOpenApi;

    @Autowired
    private ExchangisJobDsBindMapper jobDataSourceBindMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createProject(ExchangisProjectInfo projectVo, String userName) {
        // Construct the entity
        ExchangisProject project = new ExchangisProject();
        project.setName(projectVo.getName());
        project.setDescription(projectVo.getDescription());
        project.setDomain(Optional.ofNullable(projectVo.getDomain()).orElse(ExchangisProject.Domain.STANDALONE.name()));
        project.setLabels(projectVo.getLabel());
        project.setSourceMap(projectVo.getSource());
        project.setViewUsers(projectVo.getViewUsers());
        project.setEditUsers(projectVo.getEditUsers());
        project.setExecUsers(projectVo.getExecUsers());
        project.setCreateUser(userName);
        project.setCreateTime(Calendar.getInstance().getTime());
        this.projectMapper.insertOne(project);

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
    public void updateProject(ExchangisProjectInfo projectVo, String userName) {
        ExchangisProject updatedProject = new ExchangisProject();
        updatedProject.setId(Long.valueOf(projectVo.getId()));
        updatedProject.setName(projectVo.getName());
        updatedProject.setDescription(projectVo.getDescription());
        updatedProject.setLabels(projectVo.getLabel());
        updatedProject.setViewUsers(projectVo.getViewUsers());
        updatedProject.setEditUsers(projectVo.getEditUsers());
        updatedProject.setExecUsers(projectVo.getExecUsers());
        updatedProject.setDescription(projectVo.getDescription());
        // Set the updated properties
        updatedProject.setLastUpdateUser(userName);
        updatedProject.setLastUpdateTime(Calendar.getInstance().getTime());
        this.projectMapper.updateOne(updatedProject);

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

        this.projectUserMapper.deleteProjectUser(Long.valueOf(projectVo.getId()));
        if(projectUserMap.size() > 0) {
            this.projectUserMapper.addProjectUser(new ArrayList<>(projectUserMap.values()));
        }
    }

    @Override
    public PageResult<ExchangisProjectInfo> queryProjects(ProjectQueryVo queryVo) {
        PageHelper.startPage(queryVo.getPage(), queryVo.getPageSize());
        try{
            List<ExchangisProject> projects = this.projectMapper.queryPageList(queryVo);
            PageInfo<ExchangisProject> pageInfo = new PageInfo<>(projects);
            List<ExchangisProjectInfo> infoList = projects
                    .stream().map(ExchangisProjectInfo::new).collect(Collectors.toList());
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
            ExchangisProjectInfo projectVo = new ExchangisProjectInfo(project);
            projectVo.setViewUsers(project.getViewUsers());
            projectVo.setEditUsers(project.getEditUsers());
            projectVo.setExecUsers(project.getExecUsers());
            projectVo.setSource(project.getSourceMap());
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
            return new ExchangisProjectInfo(project);
        }
        return null;
    }

    @Override
    public ExchangisProjectInfo selectByName(String name){
        ExchangisProject project = this.projectMapper.selectByName(name);
        if (Objects.nonNull(project)){
            return new ExchangisProjectInfo(project);
        }
        return null;
    }

    @Override
    public ExchangisProjectUser queryProjectUser(ExchangisProjectUserVo exchangisProjectUserVo) {
        ExchangisProjectUser projectUser = new ExchangisProjectUser(Long.valueOf(exchangisProjectUserVo.getProjectId()), exchangisProjectUserVo.getPrivUser());
        return this.projectUserMapper.queryProjectUser(projectUser);
    }
}
