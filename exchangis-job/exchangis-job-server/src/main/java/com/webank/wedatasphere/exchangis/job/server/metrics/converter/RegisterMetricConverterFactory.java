package com.webank.wedatasphere.exchangis.job.server.metrics.converter;

import com.webank.wedatasphere.exchangis.job.server.metrics.MetricsVo;

/**
 * Registrable Converter factory
 * @param <T>
 */
public interface RegisterMetricConverterFactory<T extends MetricsVo> extends MetricConverterFactory<T> {

    /**
     * Register method
     * @param engineType engine type
     * @param converter converter
     */
    void register(String engineType, MetricsConverter<T> converter);
}
