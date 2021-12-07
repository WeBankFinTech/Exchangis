package com.webank.wedatasphere.exchangis.project.server.restful;

import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectDTO;
import com.webank.wedatasphere.exchangis.project.server.dto.ExchangisProjectRelationDTO;
import com.webank.wedatasphere.exchangis.project.server.entity.ExchangisProjectRelation;
import com.webank.wedatasphere.exchangis.project.server.service.ExchangisProjectRelationService;
import com.webank.wedatasphere.exchangis.project.server.utils.ExchangisProjectRestfulUtils;
import com.webank.wedatasphere.linkis.server.Message;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Component
@Path("/exchangis/relation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExchangisProjectRelationRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectRelationRestful.class);

    @Autowired
    private ExchangisProjectRelationService projectRelationService;

    @POST
    public Response save(@Valid ExchangisProjectRelationDTO dto) {
        this.projectRelationService.save(dto);
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/nodeid/{nodeId}")
    public Response getByNodeId(@PathVariable("nodeId") Long nodeId) {
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
        return Message.messageToResponse(Message.ok().data("data", dto));
    }


}
