package com.webank.wedatasphere.exchangis.job.server.restful;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisMetricsService;
import org.apache.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "dss/exchangis/main", produces = {"application/json;charset=utf-8"})
public class ExchangisMetricsRestfulApi {

    private final ExchangisMetricsService exchangisMetricsService;

    @Autowired
    public ExchangisMetricsRestfulApi(ExchangisMetricsService exchangisMetricsService) {
        this.exchangisMetricsService = exchangisMetricsService;
    }

    // get task state metrics
    @RequestMapping( value = "metrics/taskstate", method = RequestMethod.GET)
    public Message getTaskStateMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getTaskStateMetrics(request);
    }

    // get task process metrics
    @RequestMapping( value = "metrics/taskprocess", method = RequestMethod.GET)
    public Message getTaskProcessMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getTaskProcessMetrics(request);
    }

    // get datasource flow metrics
    @RequestMapping( value = "metrics/datasourceflow", method = RequestMethod.GET)
    public Message getDataSourceFlowMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getDataSourceFlowMetrics(request);
    }

    // get engine (sqoop datax linkis etc.) resource metrics
    @RequestMapping( value = "metrics/engineresource", method = RequestMethod.GET)
    public Message getEngineResourceMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getEngineResourceMetrics(request);
    }

    @RequestMapping( value = "metrics/engineresourcecpu", method = RequestMethod.GET)
    public Message getEngineResourceCpuMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getEngineResourceCpuMetrics(request);
    }

    @RequestMapping( value = "metrics/engineresourcemem", method = RequestMethod.GET)
    public Message getEngineResourceMemMetrics(HttpServletRequest request) throws Exception {
        return this.exchangisMetricsService.getEngineResourceMemMetrics(request);
    }

}
