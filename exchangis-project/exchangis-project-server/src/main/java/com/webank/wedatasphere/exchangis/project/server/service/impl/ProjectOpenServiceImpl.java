package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectDsRelationMapper;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectUserMapper;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import com.webank.wedatasphere.exchangis.project.provider.utils.ProjectAuthorityUtils;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implement of open service
 */
@Service
public class ProjectOpenServiceImpl implements ProjectOpenService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectOpenService.class);
    /**
     * Basic project service
     */
    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectDsRelationMapper projectDsRelationMapper;

    @Override
    public ExchangisProjectInfo getProject(Long projectId) {
        return projectService.getProjectDetailById(projectId);
    }

    @Override
    public boolean hasAuthority(String username, Long projectId, OperationType operationType) {
        return hasAuthority(username,
                projectService.getProjectDetailById(projectId), operationType);
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
        ExchangisProjectDsRelation dsRelation = projectDsRelationMapper
                .getByUserAndDsId(username, dataSourceId);
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
                    this.projectDsRelationMapper.queryPageList(queryVo);
            PageInfo<ExchangisProjectDsRelation> pageInfo = new PageInfo<>(dsRelationList);
            PageResult<ExchangisProjectDsRelation> pageResult = new PageResult<>();
            pageResult.setList(dsRelationList);
            pageResult.setTotal(pageInfo.getTotal());
            return pageResult;
        }finally {
            PageHelper.clearPage();
        }
    }
}
