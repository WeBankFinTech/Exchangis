package com.webank.wedatasphere.exchangis.job.server.service;


import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;

public interface ExchangisMetricsService {
    Message getTaskStateMetrics(HttpServletRequest request);

    Message getTaskProcessMetrics(HttpServletRequest request);

    Message getDataSourceFlowMetrics(HttpServletRequest request);

    Message getEngineResourceCpuMetrics(HttpServletRequest request);

    Message getEngineResourceMemMetrics(HttpServletRequest request);

    Message getEngineResourceMetrics(HttpServletRequest request);
}
