package com.webank.wedatasphere.exchangis.project.server.restful.external;

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
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectAppVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.exchangis.project.server.utils.ProjectAuthorityUtils;
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

/**
 * Restful class for dss project
 */
@RestController
@RequestMapping(value = "/dss/exchangis/main/appProject", produces = {"application/json;charset=utf-8"})
public class ExchangisProjectDssAppConnRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisProjectDssAppConnRestfulApi.class);

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

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Message createProject(@Validated @RequestBody ExchangisProjectAppVo project,
                                 BindingResult result, HttpServletRequest request){
        ExchangisProjectInfo projectVo = new ExchangisProjectInfo(project);
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }

        String oringinUser = SecurityFilter.getLoginUsername(request);
        String username = UserUtils.getLoginUser(request);
        if (StringUtils.isBlank(projectVo.getViewUsers()) || !StringUtils.contains(projectVo.getViewUsers(), username)) {
            projectVo.setViewUsers(username + projectVo.getViewUsers());
        }
        if (StringUtils.isBlank(projectVo.getEditUsers()) || !StringUtils.contains(projectVo.getEditUsers(), username)) {
            projectVo.setEditUsers(username + projectVo.getEditUsers());
        }
        if (StringUtils.isBlank(projectVo.getExecUsers()) || !StringUtils.contains(projectVo.getExecUsers(), username)) {
            projectVo.setExecUsers(username + projectVo.getExecUsers());

        }

        try {
            LOG.info("CreateProject from DSS AppConn, vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            if (projectService.existsProject(null, projectVo.getName())){
                return Message.error("Have the same name project (存在同名工程)");
            }
            long projectIdd = projectService.createProject(projectVo, username);
            String projectId = String.valueOf(projectIdd);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, String.valueOf(projectId), "Project name is: " + projectVo.getName(), OperateTypeEnum.CREATE, request);
            return ExchangisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", projectVo.getName()),
                    new Pair<>("projectId", projectId));
        } catch (Exception t) {
            LOG.error("Failed to create project for user {} from DSS", username, t);
            return Message.error("Fail to create project from DSS(创建工程失败)");
        }
    }


    /**
     * Update project
     * @param request request
     * @param projectVo project vo
     * @return
     */
    @RequestMapping( value = "/{id:\\d+}", method = RequestMethod.PUT)
    public Message updateProject(@PathVariable("id") Long id, @Validated({UpdateGroup.class, Default.class}) @RequestBody ExchangisProjectInfo projectVo
            , BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String username = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectStored = projectService.getProjectDetailById(Long.valueOf(projectVo.getId()));
            if (!ProjectAuthorityUtils.hasProjectAuthority(username, projectStored, OperationType.PROJECT_ALTER)) {
                return Message.error("You have no permission to update (没有项目的更新权限)");
            }

            LOG.info("UpdateProject vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            projectService.updateProject(projectVo, username);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, id.toString(), "Project name is: " + projectVo.getName(), OperateTypeEnum.UPDATE, request);
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
     * @param name project name
     * @return
     */
    @RequestMapping( value = "/{name}", method = RequestMethod.POST)
    public Message deleteProject(HttpServletRequest request, @PathVariable("name") String name) {
        String username = UserUtils.getLoginUser(request);
        String oringinUser = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.selectByName(name);
            if (!ProjectAuthorityUtils.hasProjectAuthority(username, projectInfo, OperationType.PROJECT_ALTER)) {
                return Message.error("You have no permission to delete (删除项目失败)");
            }

            // 校验是否有任务
            ExchangisJobQueryVo queryVo = new ExchangisJobQueryVo(Long.parseLong(projectInfo.getId()), null, null);
            PageResult<ExchangisJobVo> exchangisJobVoPageResult = jobInfoService.queryJobList(queryVo);
            if (Objects.nonNull(exchangisJobVoPageResult) && Objects.nonNull(exchangisJobVoPageResult.getList())
                    && exchangisJobVoPageResult.getList().size() > 0) {
                return Message.error("Jobs already exist under this project and the project cannot be deleted (该项目下已存在子任务，无法删除)");
            }

            projectService.deleteProjectByName(name);
            AuditLogUtils.printLog(oringinUser, username, TargetTypeEnum.PROJECT, "", "Project name is: " + name, OperateTypeEnum.DELETE, request);
            return ExchangisProjectRestfulUtils.dealOk("删除工程成功");
        } catch (Exception t) {
            LOG.error("Failed to delete project for user {}", username, t);
            return Message.error("Failed to delete project (删除工程失败)");
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
            return ExchangisProjectRestfulUtils.dealOk("根据名字获取工程成功",
                    new Pair<>("projectInfo",projectInfo));
        } catch (Exception t) {
            LOG.error("Failed to delete project for user {}", username, t);
            return Message.error("Failed to delete project (根据名字获取工程失败)");
        }
    }

}
