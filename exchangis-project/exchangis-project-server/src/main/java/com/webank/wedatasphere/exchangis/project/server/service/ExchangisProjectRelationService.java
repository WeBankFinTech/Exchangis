package com.webank.wedatasphere.exchangis.project.server.service;

import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectRelationDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProjectRelation;

import java.util.Optional;

public interface ExchangisProjectRelationService {

    void save(ExchangisProjectRelationDTO dto);
    Optional<ExchangisProjectRelation> getByNodeId(Long nodeId);

}
