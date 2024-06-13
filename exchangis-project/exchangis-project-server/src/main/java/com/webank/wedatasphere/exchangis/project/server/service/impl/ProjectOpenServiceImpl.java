package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.exception.ExchangisProjectErrorException;
import com.webank.wedatasphere.exchangis.project.provider.mapper.ProjectDsRelationMapper;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import com.webank.wedatasphere.exchangis.project.provider.utils.ProjectAuthorityUtils;
import com.webank.wedatasphere.exchangis.project.server.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
    public boolean hasAuthority(String username, Long projectId, OperationType operationType) {
        ExchangisProjectInfo project = projectService.getProjectDetailById(projectId);
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
