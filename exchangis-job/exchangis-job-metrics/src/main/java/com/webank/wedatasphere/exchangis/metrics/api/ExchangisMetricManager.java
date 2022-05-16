package com.webank.wedatasphere.exchangis.metrics.api;

import com.webank.wedatasphere.exchangis.metrics.Metric;
import com.webank.wedatasphere.exchangis.metrics.impl.ExchangisMetricRegistry;

public class ExchangisMetricManager implements IMetricManager {

    private volatile boolean enabled;
    private final ExchangisMetricRegistry exchangisMetricRegistry;

    public ExchangisMetricManager() {
        this.exchangisMetricRegistry = new ExchangisMetricRegistry();
        enabled = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Counter getCounter(MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getCounter(name);
        }

        return this.exchangisMetricRegistry.counter(name);
    }

    @Override
    public Counter getJdbcCounter(MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getJdbcCounter(name);
        }

        return this.exchangisMetricRegistry.jdbcCounter(name);
    }

    @Override
    public void register(MetricName name, Metric<?> metric) {
        if (!this.enabled) {
            return;
        }
        this.exchangisMetricRegistry.register(name, metric);
    }

}
