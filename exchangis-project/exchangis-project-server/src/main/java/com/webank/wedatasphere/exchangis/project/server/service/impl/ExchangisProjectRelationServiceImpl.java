package com.webank.wedatasphere.exchangis.project.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.exchangis.project.server.dao.ExchangisProjectRelationMapper;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectRelationDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProjectRelation;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ExchangisProjectRelationServiceImpl implements ExchangisProjectRelationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectRelationServiceImpl.class);

    @Autowired
    private ExchangisProjectRelationMapper projectRelationMapper;

    @Transactional
    @Override
    public void save(ExchangisProjectRelationDTO dto) {
        Optional<ExchangisProjectRelation> optional = this.getByNodeId(Long.parseLong(dto.getNodeId()));
        ExchangisProjectRelation relation = optional.orElseGet(ExchangisProjectRelation::new);
        relation.setProjectId(Long.parseLong(dto.getProjectId()));
        relation.setNodeId(Long.parseLong(dto.getNodeId()));
        relation.setProjectVersion(dto.getProjectVersion());
        relation.setFlowVersion(dto.getFlowVersion());
        relation.setResourceId(Long.parseLong(dto.getResourceId()));
        relation.setVersion(dto.getVersion());
        if (optional.isPresent()) {
            this.projectRelationMapper.updateById(relation);
        } else {
            this.projectRelationMapper.insert(relation);
        }
    }

    @Override
    public Optional<ExchangisProjectRelation> getByNodeId(Long nodeId) {
        QueryWrapper<ExchangisProjectRelation> query = new QueryWrapper<>();
        query.eq("node_id", nodeId);
        ExchangisProjectRelation relation = this.projectRelationMapper.selectOne(query);
        return Optional.ofNullable(relation);
    }

}
