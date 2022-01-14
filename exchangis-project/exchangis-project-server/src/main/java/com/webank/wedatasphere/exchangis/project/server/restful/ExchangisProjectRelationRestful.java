package com.webank.wedatasphere.exchangis.project.server.restful;

import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectRelationDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProjectRelation;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectRelationService;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/exchangis/relation", produces = {"application/json;charset=utf-8"})
public class ExchangisProjectRelationRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectRelationRestful.class);

    @Autowired
    private ExchangisProjectRelationService projectRelationService;

    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message save(@Valid @RequestBody ExchangisProjectRelationDTO dto) {
        this.projectRelationService.save(dto);
        return Message.ok();
    }

    @RequestMapping( value = "/nodeid/{nodeId}", method = RequestMethod.GET)
    public Message getByNodeId(@PathVariable("nodeId") Long nodeId) {
        Optional<ExchangisProjectRelation> optional = this.projectRelationService.getByNodeId(nodeId);
        ExchangisProjectRelationDTO dto = null;
        if (optional.isPresent()) {
            ExchangisProjectRelation r = optional.get();
            dto = new ExchangisProjectRelationDTO();
            dto.setId(String.valueOf(r.getId()));
            dto.setProjectId(String.valueOf(r.getProjectId()));
            dto.setNodeId(String.valueOf(r.getNodeId()));
            dto.setProjectVersion(r.getProjectVersion());
            dto.setResourceId(String.valueOf(r.getResourceId()));
            dto.setFlowVersion(r.getFlowVersion());
            dto.setVersion(r.getVersion());
        }
        return Message.ok().data("data", dto);
    }


}
