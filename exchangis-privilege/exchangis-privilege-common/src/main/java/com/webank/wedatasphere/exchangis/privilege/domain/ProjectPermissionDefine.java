package com.webank.wedatasphere.exchangis.privilege.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectService;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectPermissionDefine extends AbstractPermissionInfo<ProjectService> {

    public ProjectPermissionDefine() {
    }

    @Override
    public void setIdentify(ApplicationContext context, String username) {
        this.service = context.getBean(ProjectService.class);
        ProjectQueryVo queryVo = new ProjectQueryVo();
        queryVo.setCreateUser(username);
        PageResult<ExchangisProjectInfo> queryResult = getService().queryProjects(queryVo);
        List<ExchangisProjectInfo> projects = queryResult.getList();
        this.identify = projects.stream().collect(Collectors.toMap(ExchangisProjectInfo::getId, ExchangisProjectInfo::getName));
    }
}
