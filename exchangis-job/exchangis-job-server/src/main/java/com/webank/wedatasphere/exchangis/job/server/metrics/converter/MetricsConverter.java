package com.webank.wedatasphere.exchangis.job.server.metrics.converter;


import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.metrics.MetricsVo;

import java.util.Map;

/**
 * Converter the metricMap to MetricVo
 */
public interface MetricsConverter<T extends MetricsVo> {

    /**
     * Convert method
     * @param metricMap map value
     * @return T entity extends MetricsVo
     */
    T convert(Map<String, Object> metricMap) throws ExchangisJobServerException;
}
