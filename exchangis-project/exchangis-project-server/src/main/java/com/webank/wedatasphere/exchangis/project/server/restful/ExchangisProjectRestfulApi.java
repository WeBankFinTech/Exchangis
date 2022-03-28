package com.webank.wedatasphere.exchangis.project.server.restful;


import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.server.vo.ProjectQueryVo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.util.Objects;
import java.util.Optional;


/**
 * This is the restful class for exchangis project
 */
@RestController
@RequestMapping(value = "/dss/exchangis/main", produces = {"application/json;charset=utf-8"})
public class ExchangisProjectRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectRestfulApi.class);

    /**
     * Project service
     */
    @Resource
    private ProjectService projectService;

    /**
     * Project query
     * @param request http request
     * @param queryVo query vo
     * @param current current page
     * @param size size
     * @param name name
     * @return message
     */
    @RequestMapping( value = "projects", method = {RequestMethod.POST, RequestMethod.GET})
    public Message queryProjects(HttpServletRequest request,
                                 @RequestBody ProjectQueryVo queryVo,
                                 @RequestParam(value = "current", required = false) Integer current,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 @RequestParam(value = "name", required = false) String name) {
        String username = SecurityFilter.getLoginUsername(request);
        Optional.ofNullable(current).ifPresent(queryVo::setCurrent);
        Optional.ofNullable(size).ifPresent(queryVo::setSize);
        Optional.ofNullable(name).ifPresent(queryVo::setName);
        queryVo.setCreateUser(username);
        try {
            PageResult<ExchangisProjectInfo> pageResult = projectService.queryProjects(queryVo);
            return pageResult.toMessage();
        } catch (Exception t) {
            LOG.error("Failed to query project list for user {}", username, t);
            return Message.error("Failed to query project list (获取工程列表失败)");
        }
    }

    /**
     * Project query detail by id
     * @param request http request
     * @param projectId project id
     * @return
     */
    @RequestMapping( value = "projects/{projectId:\\d+}", method = RequestMethod.GET)
    public Message queryProjectDetail(HttpServletRequest request,
                                 @PathVariable("projectId") Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo project = projectService.getProjectDetailById(projectId);
            if (Objects.isNull(project)){
                return Message.error("Not found the project (找不到对应工程)");
            }
            if (!hasAuthority(username, project)){
                return Message.error("You have no permission to query (没有工程查看权限)");
            }
            return Message.ok().data("item", project);
        } catch (Exception t) {
            LOG.error("failed to get project detail for user {}", username, t);
            return Message.error("Fail to get project detail (获取工程详情失败)");
        }
    }

    /**
     * Create project
     * @param request request
     * @param projectVo project vo
     * @param result result
     * @return
     */
    @RequestMapping(value = "createProject", method = RequestMethod.POST)
    public Message createProject(@Validated @RequestBody ExchangisProjectInfo projectVo,
                                 BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String username = SecurityFilter.getLoginUsername(request);
        try {
            if (projectService.existsProject(null, projectVo.getName())){
                return Message.error("Have the same name project (存在同名工程)");
            }
            LOG.info("CreateProject vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            long projectId = projectService.createProject(projectVo, username);
            return ExchangisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", projectVo.getName()),
                    new Pair<>("projectId", projectId));
        } catch (Exception t) {
            LOG.error("Failed to create project for user {}", username, t);
            return Message.error("Fail to create project (创建工程失败)");
        }
    }


    /**
     * Update project
     * @param request request
     * @param projectVo project vo
     * @return
     */
    @RequestMapping( value = "updateProject", method = RequestMethod.PUT)
    public Message updateProject(@Validated({UpdateGroup.class, Default.class}) @RequestBody ExchangisProjectInfo projectVo
            , BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectStored = projectService.getProjectById(Long.valueOf(projectVo.getId()));
            if (!hasAuthority(username, projectStored)){
                return Message.error("You have no permission to update (没有项目的更新权限)");
            }
            String domain = projectStored.getDomain();
            if (StringUtils.isNotBlank(domain) && !ExchangisProject.Domain.STANDALONE.name()
                    .equalsIgnoreCase(domain)){
                return Message.error("Cannot update the outer project (无法更新来自 " + domain + " 的外部项目)");
            }
            LOG.info("UpdateProject vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            projectService.updateProject(projectVo, username);
            return ExchangisProjectRestfulUtils.dealOk("更新工程成功",
                    new Pair<>("projectName", projectVo.getName()),
                    new Pair<>("projectId", projectVo.getId()));
        } catch (Exception t) {
            LOG.error("Failed to update project for user {}", username, t);
            return Message.error("Fail to update project (更新工程失败)");
        }
    }

    /**
     * Delete project
     * @param request http request
     * @param id project id
     * @return
     */
    @RequestMapping( value = "/projects/{id:\\d+}", method = RequestMethod.DELETE)
    public Message deleteProject(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.getProjectById(id);
            if (!hasAuthority(username, projectInfo)){
                return Message.error("You have no permission to delete (删除工程失败)");
            }
            String domain = projectInfo.getDomain();
            if (StringUtils.isNotBlank(domain) && !ExchangisProject.Domain.STANDALONE.name()
                    .equalsIgnoreCase(domain)){
                return Message.error("Cannot delete the outer project (无法删除来自 " + domain + " 的外部项目)");
            }
            projectService.deleteProject(id);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (Exception t) {
            LOG.error("Failed to delete project for user {}", username, t);
            return Message.error("Failed to delete project (删除工程失败)");
        }
    }

    /**
     * TODO complete the authority strategy
     * @param username username
     * @param project project
     * @return
     */
    private boolean hasAuthority(String username, ExchangisProjectInfo project){
        return Objects.nonNull(project) && username.equals(project.getCreateUser());
    }
}
