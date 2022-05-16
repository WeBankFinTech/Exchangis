package com.webank.wedatasphere.exchangis.metrics.impl;

import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricName;

import java.util.Date;
import java.util.Optional;

public class CounterImpl implements Counter {

    private final MetricName name;

    @Override
    public MetricName getMetricName() {
        return name;
    }

    public CounterImpl(MetricName name) {
        this.name = name;
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }

    @Override
    public void inc() {

    }

    @Override
    public void inc(long n) {

    }

    @Override
    public void dec() {

    }

    @Override
    public void dec(long n) {

    }

    @Override
    public long getCount() {
        return 0;
    }
}
