package com.webank.wedatasphere.exchangis.project.server.restful;


import com.webank.wedatasphere.exchangis.common.AuditLogUtils;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.enums.OperateTypeEnum;
import com.webank.wedatasphere.exchangis.common.enums.TargetTypeEnum;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.job.server.service.JobInfoService;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.domain.ExchangisProjectUser;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ProjectAuthorityUtils;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectConfiguration;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectUserVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;
import org.apache.commons.lang3.StringUtils;
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
     * JobInfo service
     */
    @Resource
    private JobInfoService jobInfoService;

    /**
     * Project query
     * @param request http request
     * @param queryVo query vo
     * @param current current page
     * @param size size
     * @return message
     */
    @RequestMapping( value = "projects", method = {RequestMethod.POST, RequestMethod.GET})
    public Message queryProjects(HttpServletRequest request,
                                 @RequestBody ProjectQueryVo queryVo,
                                 @RequestParam(value = "current", required = false) Integer current,
                                 @RequestParam(value = "size", required = false) Integer size) {
        String username = UserUtils.getLoginUser(request);
        String name = queryVo.getName();
        if (StringUtils.isNotBlank(name)) {
            name = name.replaceAll("_", "/_");
        }
        Optional.ofNullable(current).ifPresent(queryVo::setCurrent);
        Optional.ofNullable(size).ifPresent(queryVo::setSize);
        Optional.ofNullable(name).ifPresent(queryVo::setName);
        queryVo.setCreateUser(username);
        try {
            PageResult<ExchangisProjectInfo> pageResult = projectService.queryProjects(queryVo);
            return pageResult.toMessage();
        } catch (Exception t) {
            LOG.error("Failed to query project list for user {}", username, t);
            return Message.error("Failed to query project list (获取项目列表失败)");
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
                return Message.error("Not found the project (找不到对应项目)");
            }
            if (!ProjectAuthorityUtils.hasProjectAuthority(username, project, OperationType.PROJECT_QUERY)){
                return Message.error("You have no permission to query (没有项目查看权限)");
            }
            return Message.ok().data("item", project);
        } catch (Exception t) {
            LOG.error("failed to get project detail for user {}", username, t);
            return Message.error("Fail to get project detail (获取项目详情失败)");
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
        if (ExchangisProjectConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to create (没有项目创建权限)");
        }
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }

        String username = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        if (StringUtils.isBlank(projectVo.getViewUsers()) || !StringUtils.contains(projectVo.getViewUsers(), username)) {
            projectVo.setViewUsers(username + "," + projectVo.getViewUsers());
        }
        if (StringUtils.isBlank(projectVo.getEditUsers()) || !StringUtils.contains(projectVo.getEditUsers(), username)) {
            projectVo.setEditUsers(username + "," + projectVo.getEditUsers());
        }
        if (StringUtils.isBlank(projectVo.getExecUsers()) || !StringUtils.contains(projectVo.getExecUsers(), username)) {
            projectVo.setExecUsers(username + "," + projectVo.getExecUsers());
        }

        try {
            if (projectService.existsProject(null, projectVo.getName())){
                return Message.error("Have the same name project (存在同名项目)");
            }
            LOG.info("CreateProject vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            long projectId = projectService.createProject(projectVo, username);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, String.valueOf(projectId), "Project name is: " + projectVo.getName(), OperateTypeEnum.CREATE, request);
            return ExchangisProjectRestfulUtils.dealOk("创建项目成功",
                    new Pair<>("projectName", projectVo.getName()),
                    new Pair<>("projectId", projectId));
        } catch (Exception t) {
            LOG.error("Failed to create project for user {}", username, t);
            return Message.error("Fail to create project (创建项目失败)");
        }
    }
    /**
     * check project name
     * @param request http request
     * @param name project name
     * @return
     */
    @RequestMapping( value = "/check/{name}", method = RequestMethod.POST)
    public Message getProjectByName(HttpServletRequest request, @PathVariable("name") String name) {
        String username = UserUtils.getLoginUser(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.selectByName(name);
            return ExchangisProjectRestfulUtils.dealOk("根据名字获取项目成功",
                    new Pair<>("projectInfo",projectInfo));
        } catch (Exception t) {
            LOG.error("Failed to get project for user {}", username, t);
            return Message.error("Failed to get project (根据名字获取项目失败)");
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
        if (ExchangisProjectConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有项目更新权限)");
        }
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String username = UserUtils.getLoginUser(request);
        //String username = SecurityFilter.getLoginUsername(request);
        if (StringUtils.isBlank(projectVo.getViewUsers()) || !StringUtils.contains(projectVo.getViewUsers(), username)) {
            projectVo.setViewUsers(username + "," + projectVo.getViewUsers());
        }
        if (StringUtils.isBlank(projectVo.getEditUsers()) || !StringUtils.contains(projectVo.getEditUsers(), username)) {
            projectVo.setEditUsers(username + "," + projectVo.getEditUsers());
        }
        if (StringUtils.isBlank(projectVo.getExecUsers()) || !StringUtils.contains(projectVo.getExecUsers(), username)) {
            projectVo.setExecUsers(username + "," + projectVo.getExecUsers());
        }

        try {
            ExchangisProjectInfo projectStored = projectService.getProjectDetailById(Long.valueOf(projectVo.getId()));
            if (!ProjectAuthorityUtils.hasProjectAuthority(username, projectStored, OperationType.PROJECT_ALTER)) {
                return Message.error("You have no permission to update (没有项目的更新权限)");
            }

            String domain = projectStored.getDomain();
            if (StringUtils.isNotBlank(domain) && !ExchangisProject.Domain.STANDALONE.name()
                    .equalsIgnoreCase(domain)){
                return Message.error("Cannot update the outer project (无法更新来自 " + domain + " 的外部项目)");
            }

            LOG.info("UpdateProject vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            projectService.updateProject(projectVo, username);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, projectVo.getId(), "Project name is: " + projectVo.getName(), OperateTypeEnum.UPDATE, request);
            return ExchangisProjectRestfulUtils.dealOk("更新项目成功",
                    new Pair<>("projectName", projectVo.getName()),
                    new Pair<>("projectId", projectVo.getId()));
        } catch (Exception t) {
            LOG.error("Failed to update project for user {}", username, t);
            return Message.error("Fail to update project (更新项目失败)");
        }
    }

    /**
     * Delete project
     * @param request http request
     * @param id project id
     * @return
     */
    @DeleteMapping( value = "/projects/{id:\\d+}")
    public Message deleteProject(HttpServletRequest request, @PathVariable("id") Long id) {
        if (ExchangisProjectConfiguration.LIMIT_INTERFACE.getValue()) {
            return Message.error("You have no permission to update (没有编辑权限，无法删除项目)");
        }
        String oringinUser = SecurityFilter.getLoginUsername(request);
        String username = UserUtils.getLoginUser(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.getProjectDetailById(id);
            if (!ProjectAuthorityUtils.hasProjectAuthority(username, projectInfo, OperationType.PROJECT_ALTER)) {
                return Message.error("You have no permission to delete (没有权限删除项目!)");
            }

            String domain = projectInfo.getDomain();
            if (StringUtils.isNotBlank(domain) && !ExchangisProject.Domain.STANDALONE.name()
                    .equalsIgnoreCase(domain)){
                return Message.error("Cannot delete the outer project (无法删除来自 " + domain + " 的外部项目)");
            }

            // 校验是否有任务
            ExchangisJobQueryVo queryVo = new ExchangisJobQueryVo(id, null, null);
            PageResult<ExchangisJobVo> exchangisJobVoPageResult = jobInfoService.queryJobList(queryVo);
            if (Objects.nonNull(exchangisJobVoPageResult) && Objects.nonNull(exchangisJobVoPageResult.getList())
                    && exchangisJobVoPageResult.getList().size() > 0) {
                return Message.error("Jobs already exist under this project and the project cannot be deleted (该项目下已存在子任务，无法删除)");
            }

            projectService.deleteProject(id);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, id.toString(), "Project", OperateTypeEnum.DELETE, request);
            return ExchangisProjectRestfulUtils.dealOk("删除项目成功");
        } catch (Exception t) {
            LOG.error("Failed to delete project for user {}", username, t);
            return Message.error("Failed to delete project (删除项目失败)");
        }
    }

    /**
     * get project permission
     * @param request http request
     * @param id project id
     * @return
     */
    @RequestMapping( value = "/getProjectPermission/{id:\\d+}", method = RequestMethod.GET)
    public Message getProjectPermission(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectUserVo exchangisProjectUserVo = new ExchangisProjectUserVo(id, username);
            ExchangisProjectUser exchangisProjectUser = projectService.queryProjectUser(exchangisProjectUserVo);

            return ExchangisProjectRestfulUtils.dealOk("根据项目ID和用户获取项目权限信息成功",
                    new Pair<>("exchangisProjectUser", new ExchangisProjectUserVo(exchangisProjectUser)));
        } catch (Exception t) {
            LOG.error("Failed to get exchangisProjectUser for project {} and privUser {}", id, username);
            return Message.error("Failed to get project (根据项目ID和用户获取项目权限信息失败)");
        }
    }

}
