package com.webank.wedatasphere.exchangis.project.server.service;


import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectDTO;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectGetDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.ProjectQueryRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ExchangisProjectService {

     ExchangisProject createProject(String username, CreateProjectRequest createProjectRequest) throws ExchangisProjectErrorException;

     ExchangisProject updateProject(String username, UpdateProjectRequest updateProjectRequest) throws ExchangisProjectErrorException;

     List<ExchangisProjectDTO> queryProjects(ProjectQueryRequest projectQueryRequest);

     void deleteProject(HttpServletRequest request, String id);

     void deleteProjectByDss(HttpServletRequest request, String dssProjectId);

     ExchangisProjectGetDTO getProjectById(String projectId);

     ExchangisProjectGetDTO getProjectByDssId(String dssProjectId);

//     ExchangisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws ExchangisProjectErrorException;
//
//
//     void updateProject(UpdateStreamProjectRequest updateStreamProjectRequest) throws ExchangisProjectErrorException;
//
//
//     void deleteProject(DeleteStreamProjectRequest deleteStreamProjectRequest) throws ExchangisProjectErrorException;


}
