package com.webank.wedatasphere.exchangis.metrics.impl;

import com.webank.wedatasphere.exchangis.metrics.Metric;
import com.webank.wedatasphere.exchangis.metrics.MetricBuilder;
import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricName;
import com.webank.wedatasphere.exchangis.metrics.api.MetricRegistry;
import com.webank.wedatasphere.exchangis.metrics.api.NOPMetricManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ExchangisMetricRegistry implements MetricRegistry {

    private static final int DEFAULT_MAX_METRIC_COUNT = Integer.getInteger("exchangis.maxMetricCountPerRegistry", 5000);

    private final ConcurrentMap<String, MetricBuilder<? extends Metric<?>>> metricBuilders;
    private final ConcurrentMap<String, Metric<?>> metrics;
    private final int maxMetricCount;

    public ExchangisMetricRegistry() {
        this(DEFAULT_MAX_METRIC_COUNT);
    }

    public ExchangisMetricRegistry(int maxMetricCount) {
        this.metrics = new ConcurrentHashMap<>();
        this.metricBuilders = new ConcurrentHashMap<>();
        this.maxMetricCount = maxMetricCount;
    }

    /**
     * Return the {@link Counter} registered under this name; or create and register
     * a new {@link Counter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    public Counter counter(MetricName name) {
        Counter counter = getOrAdd(name, COUNTER_BUILDER);
        if (counter == null) {
            return NOPMetricManager.NOP_COUNTER;
        }
        return counter;
    }

    public Counter jdbcCounter(MetricName name) {
        Counter counter = getOrAdd(name, JDBC_COUNTER_BUILDER);
        if (counter == null) {
            return NOPMetricManager.NOP_COUNTER;
        }
        return counter;
    }

    @Override
    public Map<String, List<Metric<?>>> getMetrics() {
        return null;
    }

    @Override
    public List<Metric<?>> getMetrics(String norm) {
        // TODO
        return null;
    }

    @Override
    public void addMetricBuilder(MetricBuilder<? extends Metric<?>> builder) {
        // TODO
    }

    @Override
    public <T extends Metric<?>> T register(String norm, T metric) {
        Metric<?> existing = metrics.putIfAbsent(norm, metric);
        if (null == existing) {
            onMetricAdded(norm, metric);
        } else {
            throw new IllegalArgumentException("A metric named " + norm + " already exists");
        }
        return metric;
    }

    @Override
    public <T extends Metric<?>> T register(MetricName name, T metric) {
        return register(name.getNorm(), metric);
    }

    @Override
    public Metric<?> register(String norm, Class<? extends Metric<?>> metricCls) {
        return null;
    }

    @Override
    public <T extends Metric<?>> T newMetric(String norm) {
        return null;
    }

    @Override
    public <T extends Metric<?>> T removeMetric(String norm) {
        return null;
    }

    private void onMetricAdded(String norm, Metric<?> metric) {
        // TODO
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric<?>> T getOrAdd(MetricName name, MetricBuilder<T> metricBuilder) {
        final Metric<?> metric = metrics.get(name.getNorm());
        if (metric == null) {
            try {
                T newMetric = metricBuilder.build(name);
                if (newMetric == null) return null;
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
        return (T) metric;
//        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private final MetricBuilder<Counter> COUNTER_BUILDER = new MetricBuilder<Counter>() {
        @Override
        public Counter build(MetricName name) {
            return new CounterImpl(name);
        }
    };

    private final MetricBuilder<JdbcCounterImpl> JDBC_COUNTER_BUILDER = new MetricBuilder<JdbcCounterImpl>() {
        @Override
        public JdbcCounterImpl build(MetricName name) {
            return new JdbcCounterImpl(name);
        }
    };
}
