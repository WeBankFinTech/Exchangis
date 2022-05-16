package com.webank.wedatasphere.exchangis.job.server.metrics.converter;


import com.webank.wedatasphere.exchangis.job.server.metrics.MetricsVo;

/**
 * Metric ConverterFactory
 * <T> Metrics vo
 */
public interface MetricConverterFactory<T extends MetricsVo> {

    /**
     *  getConverter
     * @param engineType engine type
     * @return converter
     */
     MetricsConverter<T> getOrCreateMetricsConverter(String engineType);
}
