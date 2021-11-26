package com.webank.wedatasphere.exchangis.metrics.api;

import com.webank.wedatasphere.exchangis.metrics.Metric;
import com.webank.wedatasphere.exchangis.metrics.MetricBuilder;

import java.util.List;
import java.util.Map;

public interface MetricRegistry {

    Map<String, List<Metric<?>>> getMetrics();

    List<Metric<?>> getMetrics(String norm);

    void addMetricBuilder(MetricBuilder<? extends Metric<?>> builder);

    <T extends Metric<?>> T register(String norm, T metric);

    <T extends Metric<?>> T register(MetricName name, T metric);

    Metric<?> register(String norm, Class<? extends Metric<?>> metricCls);

    <T extends Metric<?>> T newMetric(String norm);

    <T extends Metric<?>> T removeMetric(String norm);

}
