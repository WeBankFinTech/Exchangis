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
import com.webank.wedatasphere.exchangis.project.server.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.server.vo.ProjectQueryVo;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createProject(ExchangisProjectInfo projectVo, String userName) {
        // Construct the entity
        ExchangisProject project = new ExchangisProject();
        project.setName(projectVo.getName());
        project.setDescription(projectVo.getDescription());
        project.setDomain(Optional.ofNullable(projectVo.getDomain()).orElse(ExchangisProject.Domain.STANDALONE.name()));
        project.setLabels(projectVo.getLabels());
        project.setSourceMap(projectVo.getSource());
        project.setViewUsers(projectVo.getViewUsers());
        project.setEditUsers(projectVo.getEditUsers());
        project.setExecUsers(projectVo.getExecUsers());
        project.setCreateUser(userName);
        project.setCreateTime(Calendar.getInstance().getTime());
        return this.projectMapper.insertOne(project);
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
        updatedProject.setLabels(projectVo.getLabels());
        updatedProject.setViewUsers(projectVo.getViewUsers());
        updatedProject.setEditUsers(projectVo.getEditUsers());
        updatedProject.setExecUsers(projectVo.getExecUsers());
        // Set the updated properties
        updatedProject.setLastUpdateUser(userName);
        updatedProject.setLastUpdateTime(Calendar.getInstance().getTime());
        this.projectMapper.updateOne(updatedProject);
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


}
