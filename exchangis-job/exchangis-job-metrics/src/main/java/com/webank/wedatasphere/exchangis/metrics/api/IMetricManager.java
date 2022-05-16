package com.webank.wedatasphere.exchangis.metrics.api;

import com.webank.wedatasphere.exchangis.metrics.Metric;

public interface IMetricManager {

    /**
     * Create a {@link Counter} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param name the name of the metric
     * @return an instance of counter
     */
    Counter getCounter(MetricName name);

    Counter getJdbcCounter(MetricName name);

    /**
     * Register a customized metric to specified group.
     * @param metric the metric to register
     */
    void register(MetricName name, Metric<?> metric);

}
