package com.webank.wedatasphere.exchangis.metrics.impl;

import java.util.List;

public abstract class MetricsCollector implements Collector {

    protected final List<MetricObject> metrics;


    MetricsCollector() {
        this.metrics = null;
    }

    public List<MetricObject> build() {
        return metrics;
    }

}
