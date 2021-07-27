package com.webank.wedatasphere.exchangis.project.server.service;


import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;

public interface ExchangisProjectService {

     ExchangisProject createProject(String username, CreateProjectRequest createProjectRequest) throws ExchangisProjectErrorException;

//     ExchangisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws ExchangisProjectErrorException;
//
//
//     void updateProject(UpdateStreamProjectRequest updateStreamProjectRequest) throws ExchangisProjectErrorException;
//
//
//     void deleteProject(DeleteStreamProjectRequest deleteStreamProjectRequest) throws ExchangisProjectErrorException;


}
