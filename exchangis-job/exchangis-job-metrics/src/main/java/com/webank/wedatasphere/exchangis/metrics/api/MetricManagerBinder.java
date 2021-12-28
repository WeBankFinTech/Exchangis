package com.webank.wedatasphere.exchangis.metrics.api;

public class MetricManagerBinder {

    private static final MetricManagerBinder instance = new MetricManagerBinder();

    private final IMetricManager manager;

    private MetricManagerBinder() {
        manager = new ExchangisMetricManager();
    }

    public static MetricManagerBinder getSingleton() {
        return instance;
    }

    public IMetricManager getMetricManager() {
        return manager;
    }

}
