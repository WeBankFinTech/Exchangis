package com.webank.wedatasphere.exchangis.project.server.restful.external;

import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.AuthorityUtils;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectAppVo;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectAppVo;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectInfo;
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

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Message createProject(@Validated @RequestBody ExchangisProjectAppVo project,
                                 BindingResult result, HttpServletRequest request){
        ExchangisProjectInfo projectVo = new ExchangisProjectInfo(project);
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String username = SecurityFilter.getLoginUsername(request);
        try {
            LOG.info("CreateProject from DSS AppConn, vo: {}, userName: {}", JsonUtils.jackson().writeValueAsString(projectVo), username);
            if (projectService.existsProject(null, projectVo.getName())){
                return Message.error("Have the same name project (存在同名工程)");
            }
            long projectId = projectService.createProject(projectVo, username);
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
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectStored = projectService.getProjectById(Long.valueOf(projectVo.getId()));
            if (!AuthorityUtils.hasOwnAuthority(Long.parseLong(projectVo.getId()), username) && !AuthorityUtils.hasEditAuthority(Long.parseLong(projectVo.getId()), username)) {
                return Message.error("You have no permission to update (没有编辑权限，无法更新项目)");
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
     * @param name project name
     * @return
     */
    @RequestMapping( value = "/{name}", method = RequestMethod.POST)
    public Message deleteProject(HttpServletRequest request, @PathVariable("name") String name) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.selectByName(name);
//            if (!hasAuthority(username, projectInfo)){
//                return Message.error("You have no permission to delete (删除工程失败)");
//            }
//            String domain = projectInfo.getDomain();
//            if (StringUtils.isNotBlank(domain) && !ExchangisProject.Domain.STANDALONE.name()
//                    .equalsIgnoreCase(domain)){
//                return Message.error("Cannot delete the outer project (无法删除来自 " + domain + " 的外部项目)");
//            }
            projectService.deleteProjectByName(name);
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
        String username = SecurityFilter.getLoginUsername(request);
        try {
            ExchangisProjectInfo projectInfo = projectService.selectByName(name);
            return ExchangisProjectRestfulUtils.dealOk("根据名字获取工程成功",
                    new Pair<>("projectInfo",projectInfo));
        } catch (Exception t) {
            LOG.error("Failed to delete project for user {}", username, t);
            return Message.error("Failed to delete project (根据名字获取工程失败)");
        }


    }


    /**
     * @param username username
     * @param project project
     * @return
     */
    private boolean hasAuthority(String username, ExchangisProjectInfo project){
        return Objects.nonNull(project) && username.equals(project.getCreateUser());
    }
}
