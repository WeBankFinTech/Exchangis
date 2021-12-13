package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.project.server.dao.ExchangisProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectDTO;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectGetDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.ProjectQueryRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangisProjectServiceImpl implements ExchangisProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectServiceImpl.class);

    @Autowired
    private ExchangisProjectMapper exchangisProjectMapper;

    @Autowired
    private ExchangisJobInfoMapper exchangisJobInfoMapper;

    @Autowired
    private ExchangisJobDsBindMapper exchangisJobDsBindMapper;

    @Transactional
    @Override
    public ExchangisProject createProject(String username, CreateProjectRequest createProjectRequest) throws ExchangisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());
        String workspaceName = createProjectRequest.getWorkspaceName();
        String projectName = createProjectRequest.getProjectName();
        String dssProjectName = createProjectRequest.getDssProjectName();
        String description = createProjectRequest.getDescription();
        String tags = createProjectRequest.getTags();
        String editUsers = createProjectRequest.getEditUsers();
        String viewUsers = createProjectRequest.getViewUsers();
        String execUsers = createProjectRequest.getExecUsers();

        QueryWrapper<ExchangisProject> query = new QueryWrapper<>();
        query.eq("name", projectName);
        Integer count = this.exchangisProjectMapper.selectCount(query);
        if (count > 0) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.create.error.name.exists");
        }

        Date now = new Date();
        ExchangisProject entity = new ExchangisProject();
        entity.setCreateBy(username);
        entity.setCreateTime(now);
        Optional.ofNullable(createProjectRequest.getDssProjectId()).ifPresent(dssProjectId -> {
            if (StringUtils.isNotBlank(dssProjectId)) {
                entity.setDssProjectId(Long.parseLong(dssProjectId));
            }
        });
        entity.setWorkspaceName(workspaceName);
        entity.setName(StringUtils.isNotBlank(dssProjectName) ? dssProjectName : projectName);
        entity.setDssName(dssProjectName);
        entity.setTags(tags);
        entity.setEditUsers(editUsers);
        entity.setViewUsers(viewUsers);
        entity.setExecUsers(execUsers);
        entity.setWorkspaceName(workspaceName);
        entity.setDescription(description);
        entity.setDomain(StringUtils.isNotBlank(dssProjectName) ? ExchangisProject.Domain.WORKSPACE.name() : ExchangisProject.Domain.STANDALONE.name());

        int insert = this.exchangisProjectMapper.insert(entity);
        if (insert <= 0) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.create.error");
        }
        return entity;
    }

    @Transactional
    @Override
    public ExchangisProject updateProject(String username, UpdateProjectRequest updateProjectRequest) throws ExchangisProjectErrorException {
        ExchangisProject exchangisProject = null;
        if (null != updateProjectRequest.getId()) {
            exchangisProject = this.exchangisProjectMapper.selectById(updateProjectRequest.getId());
        } else if (StringUtils.isNotBlank(updateProjectRequest.getDssProjectName())) {
            QueryWrapper<ExchangisProject> wrapper = new QueryWrapper<>();
            wrapper.eq("dss_name", updateProjectRequest.getDssProjectName());
            exchangisProject = this.exchangisProjectMapper.selectOne(wrapper);
        }

        if (null == exchangisProject) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.update.error");
        }

        QueryWrapper<ExchangisProject> query = new QueryWrapper<>();
        query.eq("name", updateProjectRequest.getProjectName());
        query.ne("id", exchangisProject.getId());
        Integer count = this.exchangisProjectMapper.selectCount(query);
        if (count > 0) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.update.error.name.exists");
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getDescription())) {
            exchangisProject.setDescription(updateProjectRequest.getDescription());
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getProjectName())) {
            if(ExchangisProject.Domain.STANDALONE.name().equals(exchangisProject.getDomain())) {
                exchangisProject.setName(updateProjectRequest.getProjectName());
            }
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getWorkspaceName())) {
            exchangisProject.setWorkspaceName(updateProjectRequest.getWorkspaceName());
            exchangisProject.setName(updateProjectRequest.getWorkspaceName());
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getTags())) {
            exchangisProject.setTags(updateProjectRequest.getTags());
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getViewUsers())) {
            exchangisProject.setViewUsers(updateProjectRequest.getViewUsers());
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getEditUsers())) {
            exchangisProject.setExecUsers(updateProjectRequest.getEditUsers());
        }

        if (!Strings.isNullOrEmpty(updateProjectRequest.getExecUsers())) {
            exchangisProject.setExecUsers(updateProjectRequest.getExecUsers());
        }

        int ret = this.exchangisProjectMapper.updateById(exchangisProject);
        if (ret <= 0) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.update.error");
        }
        return exchangisProject;
    }

    @Override
    public List<ExchangisProjectDTO> queryProjects(ProjectQueryRequest projectQueryRequest) {
        QueryWrapper<ExchangisProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", projectQueryRequest.getUsername());

        if (!Strings.isNullOrEmpty(projectQueryRequest.getName())) {
            queryWrapper.like("name", projectQueryRequest.getName());
        }

        List<ExchangisProject> exchangisProjects = this.exchangisProjectMapper.selectList(queryWrapper);

        return exchangisProjects.stream().map(ExchangisProjectDTO::new).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public void deleteProject(HttpServletRequest request, String id) {

        QueryWrapper<ExchangisJobInfo> jobquery = new QueryWrapper<>();
        jobquery.eq("project_id", id);

        List<ExchangisJobInfo> jobs = this.exchangisJobInfoMapper.selectList(jobquery);

        if (null != jobs && jobs.size() > 0) {
            this.exchangisJobInfoMapper.delete(jobquery);

            QueryWrapper<ExchangisJobDsBind> dsBindQuery = new QueryWrapper<ExchangisJobDsBind>().in("job_id",
                    jobs.stream().map(ExchangisJobInfo::getId).toArray()
            );
            this.exchangisJobDsBindMapper.delete(dsBindQuery);
        }

        this.exchangisProjectMapper.deleteById(Long.parseLong(id));
    }

    @Transactional
    @Override
    public void deleteProjectByDss(HttpServletRequest request, String dssName) {
        ExchangisProjectGetDTO dto = this.getProjectByDssName(dssName);
        if (null != dto && null != dto.getId() && StringUtils.isNotBlank(dto.getId())) {
            this.deleteProject(request, dto.getId());
        }
    }

    @Override
    public ExchangisProjectGetDTO getProjectById(String projectId) {
        ExchangisProject project = this.exchangisProjectMapper.selectById(projectId);
        return new ExchangisProjectGetDTO(project);
    }

    @Override
    public ExchangisProjectGetDTO getProjectByDssName(String dssName) {
        QueryWrapper<ExchangisProject> wrapper = new QueryWrapper<>();
        wrapper.eq("dss_name", dssName);
        return new ExchangisProjectGetDTO(this.exchangisProjectMapper.selectOne(wrapper));
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public StreamisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws StreamisProjectErrorException {
//        LOGGER.info("user {} starts to create project {}", createStreamProjectRequest.createBy(), createStreamProjectRequest.projectName());
//        StreamisProject streamisProject = new StreamisProject(createStreamProjectRequest.projectName(), createStreamProjectRequest.description(), createStreamProjectRequest.createBy());
//        //streamisProjectMapper.insertProject(streamisProject);
//        LOGGER.info("user {} ends to create project {} and id is {}", createStreamProjectRequest.createBy(), createStreamProjectRequest.projectName(), streamisProject.getId());
//        return streamisProject;
//    }
//
//    @Override
//    public void updateProject(UpdateStreamProjectRequest updateStreamProjectRequest) throws StreamisProjectErrorException {
//        LOGGER.info("User {} begins to update project {}", updateStreamProjectRequest.updateBy(), updateStreamProjectRequest.projectName());
//
//    }
//
//
//    @Override
//    public void deleteProject(DeleteStreamProjectRequest deleteStreamProjectRequest) throws StreamisProjectErrorException {
//
//    }
}
