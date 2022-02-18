package com.webank.wedatasphere.exchangis.project.server.restful;


import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectDTO;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectGetDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.request.CreateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.request.ProjectQueryRequest;
import com.webank.wedatasphere.exchangis.project.server.request.UpdateProjectRequest;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * this is the restful class for exchangis project
 */
@RestController
@RequestMapping(value = "/exchangis", produces = {"application/json;charset=utf-8"})
public class ExchangisProjectRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectRestfulApi.class);

    @Autowired
    private ExchangisProjectService projectService;

    @RequestMapping( value = "projects", method = RequestMethod.POST)
    public Message queryProjects(HttpServletRequest request,
                                 @Valid @RequestBody ProjectQueryRequest projectQueryRequest,
                                 @RequestParam(value = "current", required = false) int current,
                                 @RequestParam(value = "size", required = false) int size) {
        String username = SecurityFilter.getLoginUsername(request);
        if (null == projectQueryRequest) {
            projectQueryRequest = new ProjectQueryRequest();
        }
        projectQueryRequest.setUsername(username);
        try {
            List<ExchangisProjectDTO> projects = projectService.queryProjects(projectQueryRequest, current, size);
            int total = projectService.count(projectQueryRequest);
            Message message = Message.ok();
            message.data("total", total);
            message.data("list", projects);
            return message;
            //return Message.ok().data("list", projects);
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return Message.error("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping( value = "projects/{projectId}", method = RequestMethod.GET)
    public Message queryProjects(HttpServletRequest request,
                                 @PathVariable("projectId") String projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectGetDTO dto = projectService.getProjectById(projectId);
            return Message.ok().data("item", dto);
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return Message.error("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping(value = "projects/dss/{dssName}", method = RequestMethod.GET)
    public Message queryProjectsByDss(HttpServletRequest request,
                                      @PathVariable("dssName") String dssName) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("queryProjectsByDss workspaceName {}", dssName);
            ExchangisProjectGetDTO dto = projectService.getProjectByDssName(dssName);
            return Message.ok().data("item", dto);
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return Message.error("获取工程列表失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping(value = "createProject", method = RequestMethod.POST)
    public Message createProject(HttpServletRequest request,
                                 @Valid @RequestBody CreateProjectRequest createProjectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("createProject createProjectRequest {}", createProjectRequest.toString());
            ExchangisProject exchangisProject = projectService.createProject(username, createProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        } catch (final Throwable t) {
            LOGGER.error("failed to create project for user {}", username, t);
            return Message.error("创建工程失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping( value = "updateProject", method = RequestMethod.PUT)
    public Message updateProject(HttpServletRequest request,
                                 @Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("updateProject updateProjectRequest {}", updateProjectRequest.toString());
            ExchangisProject exchangisProject = projectService.updateProject(username, updateProjectRequest);
            return ExchangisProjectRestfulUtils.dealOk("更新工程成功",
                    new Pair<>("projectName", exchangisProject.getName()), new Pair<>("projectId", exchangisProject.getId()));
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return Message.error("更新工程失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping( value = "/projects/{id}", method = RequestMethod.DELETE)
    public Message deleteProject(HttpServletRequest request, @PathVariable("id") String id) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            projectService.deleteProject(request, id);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return Message.error("删除工程失败,原因是:" + t.getMessage());
        }
    }

    @RequestMapping( value = "/projects/dss/{dssName}", method = RequestMethod.DELETE)
    public Message deleteProjectByDss(HttpServletRequest request, @PathVariable("dssName") String dssName) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOGGER.info("deleteProjectByDss dssName {}", dssName);
            projectService.deleteProjectByDss(request, dssName);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (final Throwable t) {
            LOGGER.error("failed to update project for user {}", username, t);
            return Message.error("删除工程失败,原因是:" + t.getMessage());
        }
    }
}
