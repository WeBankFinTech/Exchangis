package com.webank.wedatasphere.exchangis.datasource.server.controller;

//import com.webank.wedatasphere.exchangis.datasource.server.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.server.service.ExchangisDataSourceService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis")
public class ExchangisDataSourceController {

    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisDataSourceController(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    @GET
    @Path("datasources/all")
    public Response listDataSourceTypes(@Context HttpServletRequest request) {
        Message message = this.exchangisDataSourceService.listDataSources(request);
        return Message.messageToResponse(message);
    }

    @POST
    @Path("datasources/create")
    public Response create(@Context HttpServletRequest request, Map<String, Object> json) {
        Message message = this.exchangisDataSourceService.create(request, json);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/params/ui")
    public Response getParamsUI(@Context HttpServletRequest request, @PathParam("type")String type) {
        Message message = this.exchangisDataSourceService.getParamsUI(request, type);
        return Message.messageToResponse(message);
    }

}
