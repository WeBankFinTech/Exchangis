package com.webank.wedatasphere.exchangis.metrics.web;

import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricManager;
import com.webank.wedatasphere.exchangis.metrics.impl.MetricNames;
import com.webank.wedatasphere.linkis.server.Message;
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
public class ExchangisMetricController {
    private static final Counter metricTaskRunningCounter = MetricManager.getJdbcCounter(MetricNames.TASK_RUNNING_COUNT_METRIC_NAME);

//    @Resource
//    private ExchangisMetricRegistry exchangisMetricsRegister;
//
//    @Resource
//    private ExchangisMetricsService exchangisMetricsService;
//
//    @GET
//    @Path("metric/{norm}")
//    public Response getMetric(
//            @Context HttpServletRequest request,
//            @PathParam(value = "norm") String norm
//    ) {
//        List<Metric<?>> metrics = this.exchangisMetricsRegister.getMetrics(norm);
//        Message message = Message.ok().data("metrics", metrics);
//        return Message.messageToResponse(message);
//    }

    @GET
    @Path("metric/test")
    @Deprecated
    public Response test(
            @Context HttpServletRequest request
    ) {
        metricTaskRunningCounter.inc();
        Message message = Message.ok().data("count", metricTaskRunningCounter.getCount());
        return Message.messageToResponse(message);
    }

    // get task state metrics
//    @GET
//    @Path("metrics/taskstate")
//    public Response getTaskStateMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getTaskStatusMetrics(request);
//        return Message.messageToResponse(message);
//    }
//
//    // get task process metrics
//    @GET
//    @Path("metrics/taskprocess")
//    public Response getTaskProcessMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getTaskProcessMetrics(request);
//        return Message.messageToResponse(message);
//    }
//
//    // get datasource flow metrics
//    @GET
//    @Path("metrics/datasourceflow")
//    public Response getDataSourceFlowMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getDataSourceFlowMetrics(request);
//        return Message.messageToResponse(message);
//    }
//
//    // get engine (sqoop datax linkis etc.) resource metrics
//    @GET
//    @Path("metrics/engineresource")
//    public Response getEngineResourceMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getEngineResourceMetrics(request);
//        return Message.messageToResponse(message);
//    }
//
//    @GET
//    @Path("metrics/engineresourcecpu")
//    public Response getEngineResourceCpuMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getEngineResourceCpuMetrics(request);
//        return Message.messageToResponse(message);
//    }
//
//    @GET
//    @Path("metrics/engineresourcemem")
//    public Response getEngineResourceMemMetrics(@Context HttpServletRequest request) throws Exception {
//        Message message = this.exchangisMetricsService.getEngineResourceMemMetrics(request);
//        return Message.messageToResponse(message);
//    }

}
