package com.webank.wedatasphere.exchangis.job.server.metrics.converter;


import com.webank.wedatasphere.exchangis.job.server.metrics.MetricsVo;

/**
 * Metric ConverterFactory
 */
public interface MetricConverterFactory {

    /**
     *  getConverter
     * @param metricsVoClass vo class
     * @param <T> Metrics converter
     * @return
     */
    <T extends MetricsVo> MetricsConverter <T> getOrCreateMetricsConverter(Class<T> metricsVoClass, String engineType);
}
