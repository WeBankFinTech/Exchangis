package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.webank.wedatasphere.exchangis.project.server.dao.ExchangisProjectMapper;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExchangisProjectServiceImpl implements ExchangisProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectServiceImpl.class);

    @Autowired
    private ExchangisProjectMapper exchangisProjectMapper;

    @Override
    public ExchangisProject createProject(String username, CreateProjectRequest createProjectRequest) throws ExchangisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());
        return null;
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
