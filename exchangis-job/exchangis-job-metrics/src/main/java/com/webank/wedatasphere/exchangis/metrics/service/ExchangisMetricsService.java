package com.webank.wedatasphere.exchangis.metrics.service;

import com.webank.wedatasphere.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;

public interface ExchangisMetricsService {
    Message getTaskStatusMetrics(HttpServletRequest request);

    Message getTaskProcessMetrics(HttpServletRequest request);

    Message getDataSourceFlowMetrics(HttpServletRequest request);

    Message getEngineResourceCpuMetrics(HttpServletRequest request);

    Message getEngineResourceMemMetrics(HttpServletRequest request);

    Message getEngineResourceMetrics(HttpServletRequest request);
}
