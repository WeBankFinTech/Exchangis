package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectDsVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectDsRelationMapper;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectMapper;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import com.webank.wedatasphere.exchangis.project.provider.utils.ProjectAuthorityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implement of open service
 */
@Service
public class ProjectOpenServiceImpl implements ProjectOpenService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectOpenService.class);

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ProjectDsRelationMapper projectDsRelationMapper;

    @Resource
    private ProjectUserMapper projectUserMapper;

    @Override
    public ExchangisProjectInfo getProject(Long projectId) {
        ExchangisProject project = this.projectMapper.getDetailById(projectId);
        if (Objects.nonNull(project)){
            ExchangisProjectInfo projectVo = new ExchangisProjectInfo(project);
            projectVo.setDataSources(null);
            projectVo.setViewUsers(project.getViewUsers());
            projectVo.setEditUsers(project.getEditUsers());
            projectVo.setExecUsers(project.getExecUsers());
            projectVo.setSource(project.getSourceMap());
            List<ExchangisProjectDsRelation> proDsRelations = projectDsRelationMapper
                    .queryPageList(new ProjectDsQueryVo(projectId));
            projectVo.setDataSources(proDsRelations.stream()
                    .map(ExchangisProjectDsVo::new).collect(Collectors.toList()));
            return projectVo;
        }
        return null;
    }

    @Override
    public boolean hasAuthority(String username, Long projectId, OperationType operationType) {
        return hasAuthority(username, getProject(projectId), operationType);
    }

    @Override
    public boolean hasAuthority(String username, ExchangisProjectInfo project, OperationType operationType) {
        if (Objects.nonNull(project)){
            try {
                return ProjectAuthorityUtils.hasProjectAuthority(username,
                        project, operationType);
            } catch (ExchangisProjectErrorException e) {
                LOG.warn("Fail to validate the authority of project [{}] in operation [{}] for user [{}]",
                        project.getName(), operationType, username, e);
            }
        }
        return false;
    }

    @Override
    public boolean hasDataSourceAuth(String username, Long dataSourceId, Consumer<ExchangisDataSource> authority) {
        // Admin user
        ExchangisProjectDsRelation dsRelation = (GlobalConfiguration.isAdminUser(username)) ?
                projectDsRelationMapper.getByDsId(dataSourceId) : projectDsRelationMapper.getByUserAndDsId(username, dataSourceId);
        if (Objects.nonNull(dsRelation)){
            // Call back the data source authority
            authority.accept(new ExchangisDataSource() {
                @Override
                public Long getId() {
                    return dsRelation.getDsId();
                }

                @Override
                public void setId(Long id) {
                }

                @Override
                public String getType() {
                    return dsRelation.getDsType();
                }

                @Override
                public void setType(String type) {
                }

                @Override
                public String getName() {
                    return dsRelation.getDsName();
                }

                @Override
                public void setName(String name) {
                }

                @Override
                public String getCreator() {
                    return dsRelation.getDsCreator();
                }

                @Override
                public void setCreator(String creator) {
                }

                @Override
                public String getDesc() {
                    return null;
                }

                @Override
                public void setDesc(String desc) {
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public PageResult<ExchangisProjectDsRelation> queryDsRelation(ProjectDsQueryVo queryVo) {
        PageHelper.startPage(queryVo.getPage(), queryVo.getPageSize());
        try{
            List<ExchangisProjectDsRelation> dsRelationList =
                    projectDsRelationMapper.queryPageList(queryVo);
            PageInfo<ExchangisProjectDsRelation> pageInfo = new PageInfo<>(dsRelationList);
            PageResult<ExchangisProjectDsRelation> pageResult = new PageResult<>();
            pageResult.setList(dsRelationList);
            pageResult.setTotal(pageInfo.getTotal());
            return pageResult;
        }finally {
            PageHelper.clearPage();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDsRelations(List<ExchangisProjectDsRelation> relations) {
        this.projectDsRelationMapper.insertBatch(relations);
    }

    @Override
    public List<ExchangisProjectDsRelation> listByProjects(List<Long> proIds) {
        return projectDsRelationMapper.listByProjects(proIds);
    }

    @Override
    public void recycleUserProject(String username, String handover, List<Long> projectIds) {
        ProjectQueryVo projectQueryVo = new ProjectQueryVo();
        projectQueryVo.setCreateUser(username);
        projectQueryVo.setProjectIds(projectIds);
        List<ExchangisProject> projects = projectMapper.queryByUser(projectQueryVo);
        if (Objects.nonNull(projects) && !projects.isEmpty()) {
            for (ExchangisProject project : projects) {
                if (StringUtils.equals(username, project.getCreateUser())) {
                    project.setCreateUser(handover);
                }
                if (StringUtils.equals(username, project.getLastUpdateUser())) {
                    project.setLastUpdateUser(handover);
                }
                if (StringUtils.isNotBlank(project.getViewUsers())) {
                    Set<String> viewUsers = Arrays.stream(project.getViewUsers().split(",")).collect(Collectors.toSet());
                    if (viewUsers.contains(username)) {
                        viewUsers.remove(username);
                        viewUsers.add(handover);
                        project.setViewUsers(StringUtils.join(viewUsers, ","));
                    }
                }
                if (StringUtils.isNotBlank(project.getEditUsers())) {
                    Set<String> editUsers = Arrays.stream(project.getEditUsers().split(",")).collect(Collectors.toSet());
                    if (editUsers.contains(username)) {
                        editUsers.remove(username);
                        editUsers.add(handover);
                        project.setEditUsers(StringUtils.join(editUsers, ","));
                    }
                }
                if (StringUtils.isNotBlank(project.getExecUsers())) {
                    Set<String> execUsers = Arrays.stream(project.getExecUsers().split(",")).collect(Collectors.toSet());
                    if (execUsers.contains(username)) {
                        execUsers.remove(username);
                        execUsers.add(handover);
                        project.setExecUsers(StringUtils.join(execUsers, ","));
                    }
                }
            }
            projectMapper.batchUpdate(projects);
        }
        projectUserMapper.batchUpdate(username, handover);
    }
}
