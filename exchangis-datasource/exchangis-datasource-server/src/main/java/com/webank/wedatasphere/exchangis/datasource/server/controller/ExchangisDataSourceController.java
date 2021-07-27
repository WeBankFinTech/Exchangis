package com.webank.wedatasphere.exchangis.datasource.server.controller;

//import com.webank.wedatasphere.exchangis.datasource.server.service.ExchangisDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/dss/framework/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExchangisDataSourceController {

//    private final ExchangisDataSourceService exchangisDataSourceService;
//
//    @Autowired
//    public ExchangisDataSourceController(ExchangisDataSourceService exchangisDataSourceService) {
//        this.exchangisDataSourceService = exchangisDataSourceService;
//    }

    @GET
    @Path("/data_source/type/all")
    public Response listDataSourceTypes() {
//        this.exchangisDataSourceService.
        return null;
    }

}
