package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisMetricsService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis")
public class ExchangisMetricsController {

    private final ExchangisMetricsService exchangisMetricsService;

    @Autowired
    public ExchangisMetricsController(ExchangisMetricsService exchangisMetricsService) {
        this.exchangisMetricsService = exchangisMetricsService;
    }

    // get task state metrics
    @GET
    @Path("metrics/taskstate")
    public Response getTaskStateMetrics(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisMetricsService.getTaskStateMetrics(request);
        return Message.messageToResponse(message);
    }

    // get task process metrics
    @GET
    @Path("metrics/taskprocess")
    public Response getTaskProcessMetrics(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisMetricsService.getTaskProcessMetrics(request);
        return Message.messageToResponse(message);
    }

    // get datasource flow metrics
    @GET
    @Path("metrics/datasourceflow")
    public Response getDataSourceFlowMetrics(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisMetricsService.getDataSourceFlowMetrics(request);
        return Message.messageToResponse(message);
    }

    // get engine (sqoop datax linkis etc.) resource metrics
    @GET
    @Path("metrics/engineresource")
    public Response getEngineResourceMetrics(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisMetricsService.getEngineResourceMetrics(request);
        return Message.messageToResponse(message);
    }

}
