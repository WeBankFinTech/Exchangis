package com.webank.wedatasphere.exchangis.metrics;

public interface GuageMetric<T> extends Metric<T> {

    void setValue(T value);

}
