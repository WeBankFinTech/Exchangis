package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.project.server.dao.ExchangisProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ExchangisProjectServiceImpl implements ExchangisProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectServiceImpl.class);

    @Autowired
    private ExchangisProjectMapper exchangisProjectMapper;

    @Transactional
    @Override
    public ExchangisProject createProject(String username, CreateProjectRequest createProjectRequest) throws ExchangisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());

        String workspaceName = createProjectRequest.getWorkspaceName();
        String projectName = createProjectRequest.getProjectName();
        String description = createProjectRequest.getDescription();
        String tags = createProjectRequest.getTags();
        String editUsers = createProjectRequest.getEditUsers();
        String viewUsers = createProjectRequest.getViewUsers();
        String execUsers = createProjectRequest.getExecUsers();

        Date now = new Date();
        ExchangisProject entity = new ExchangisProject();
        entity.setCreateBy(username);
        entity.setCreateTime(now);
        entity.setDssProjectId(1L);
        entity.setName(projectName);
        entity.setTags(tags);
        entity.setEditUsers(editUsers);
        entity.setViewUsers(viewUsers);
        entity.setExecUsers(execUsers);
        entity.setWorkspaceName(workspaceName);
        entity.setDescription(description);

        int insert = this.exchangisProjectMapper.insert(entity);
        if (insert <= 0) {
            throw new ExchangisProjectErrorException(30041, "exchangis.project.create.error");
        }
        return entity;
    }

    @Transactional
    @Override
    public ExchangisProject updateProject(String username, UpdateProjectRequest updateProjectRequest) throws ExchangisProjectErrorException {
        ExchangisProject exchangisProject = this.exchangisProjectMapper.selectById(updateProjectRequest.getId());
        if(!Strings.isNullOrEmpty(updateProjectRequest.getDescription())) {
            exchangisProject.setDescription(updateProjectRequest.getDescription());
        }
        if (!Strings.isNullOrEmpty(updateProjectRequest.getProjectName())) {
            exchangisProject.setName(updateProjectRequest.getProjectName());
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
