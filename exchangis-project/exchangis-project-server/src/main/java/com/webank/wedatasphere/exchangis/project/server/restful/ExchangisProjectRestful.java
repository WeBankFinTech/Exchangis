package com.webank.wedatasphere.exchangis.project.server.restful;


import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * this is the restful class for exchangis project
 */
@Component
@Path("/exchangis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExchangisProjectRestful {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectRestful.class);

    @Autowired
    private ExchangisProjectService projectService;

    @POST
    @Path("createProject")
    public Response createProject(@Context HttpServletRequest request, @Valid CreateProjectRequest createProjectRequest){
        // TODO
//        String username = SecurityFilter.getLoginUsername(request);
        String username = "hdfs";
        try{
            ExchangisProject exchangisProject = projectService.createProject(username, createProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        }catch(final Throwable t){
            LOGGER.error("failed to create project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("创建工程失败,原因是:" + t.getMessage());
        }
    }


    @POST
    @Path("updateProject")
    public Response updateProject(@Context HttpServletRequest request, @Valid UpdateProjectRequest updateProjectRequest){
        // TODO
//        String username = SecurityFilter.getLoginUsername(request);

        String username = "hdfs";
        try {
            ExchangisProject exchangisProject = projectService.updateProject(username, updateProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("更新工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        } catch(final Throwable t){
            LOGGER.error("failed to update project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("更新工程失败,原因是:" + t.getMessage());
        }
    }
//
//
//    @POST
//    @Path("deleteProject")
//    public Response deleteProject(@Context HttpServletRequest request, @Valid DeleteProjectRequest deleteProjectRequest){
//        return null;
//    }





}
