package com.webank.wedatasphere.exchangis.metrics.web;

import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricManager;
import com.webank.wedatasphere.exchangis.metrics.impl.MetricNames;
import org.apache.linkis.server.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "exchangis", produces = {"application/json;charset=utf-8"})
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

    @RequestMapping( value = "metric/test", method = RequestMethod.GET)
    @Deprecated
    public Message test(HttpServletRequest request
    ) {
        metricTaskRunningCounter.inc();
        return Message.ok().data("count", metricTaskRunningCounter.getCount());
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
