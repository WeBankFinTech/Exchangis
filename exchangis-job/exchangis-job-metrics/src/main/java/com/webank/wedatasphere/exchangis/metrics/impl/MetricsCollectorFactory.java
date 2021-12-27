package com.webank.wedatasphere.exchangis.metrics.impl;

public class MetricsCollectorFactory {

    public static MetricsCollector create() {
        return new NormalMetricsCollector();
    }

}
