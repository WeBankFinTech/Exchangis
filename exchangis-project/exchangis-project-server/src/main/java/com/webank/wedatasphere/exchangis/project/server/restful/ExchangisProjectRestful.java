package com.webank.wedatasphere.exchangis.project.server.restful;


import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectDTO;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectGetDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.ProjectQueryRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


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
    @Path("projects")
    public Response queryProjects(@Context HttpServletRequest request, @Valid ProjectQueryRequest projectQueryRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        if (null == projectQueryRequest) {
            projectQueryRequest = new ProjectQueryRequest();
        }
        projectQueryRequest.setUsername(username);
        try {
            List<ExchangisProjectDTO> projects = projectService.queryProjects(projectQueryRequest);
            return Message.messageToResponse(Message.ok().data("list", projects));
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @GET
    @Path("projects/{projectId}")
    public Response queryProjects(@Context HttpServletRequest request, @PathParam("projectId") String projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectGetDTO dto = projectService.getProjectById(projectId);
            return Message.messageToResponse(Message.ok().data("item", dto));
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @GET
    @Path("projects/dss/{dssProjectId}")
    public Response queryProjectsByDss(@Context HttpServletRequest request, @PathParam("dssProjectId") String dssProjectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("queryProjectsByDss dssProjectId {}", dssProjectId);
            ExchangisProjectGetDTO dto = projectService.getProjectByDssId(dssProjectId);
            return Message.messageToResponse(Message.ok().data("item", dto));
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @POST
    @Path("createProject")
    public Response createProject(@Context HttpServletRequest request, @Valid CreateProjectRequest createProjectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("createProject createProjectRequest {}", createProjectRequest.toString());
            ExchangisProject exchangisProject = projectService.createProject(username, createProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("创建工程失败,原因是:" + t.getMessage());
        }
    }

    @PUT
    @Path("updateProject")
    public Response updateProject(@Context HttpServletRequest request, @Valid UpdateProjectRequest updateProjectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("updateProject updateProjectRequest {}", updateProjectRequest.toString());
            ExchangisProject exchangisProject = projectService.updateProject(username, updateProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("更新工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("更新工程失败,原因是:" + t.getMessage());
        }
    }

    @DELETE
    @Path("/projects/{id}")
    public Response deleteProject(@Context HttpServletRequest request, @PathParam("id") String id) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            projectService.deleteProject(request, id);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("删除工程失败,原因是:" + t.getMessage());
        }
    }

    @DELETE
    @Path("/projects/dss/{dssProjectId}")
    public Response deleteProjectByDss(@Context HttpServletRequest request, @PathParam("dssProjectId") String id) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("deleteProjectByDss dssProjectId {}", id);
            projectService.deleteProject(request, id);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return ExchangisProjectRestfulUtils.dealError("删除工程失败,原因是:" + t.getMessage());
        }
    }
}
