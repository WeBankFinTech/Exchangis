package com.webank.wedatasphere.exchangis.project.server.restful.external;

import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectAppVo;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectAppVo;
import com.webank.wedatasphere.exchangis.project.server.vo.ExchangisProjectInfo;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.common.utils.JsonUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Restful class for dss project
 */
@RestController
@RequestMapping(value = "/exchangis/dss/project", produces = {"application/json;charset=utf-8"})
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
}
