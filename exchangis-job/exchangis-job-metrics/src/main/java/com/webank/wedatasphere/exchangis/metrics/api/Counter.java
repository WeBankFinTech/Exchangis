package com.webank.wedatasphere.exchangis.metrics.api;

import com.webank.wedatasphere.exchangis.metrics.Metric;

/**
 * <pre>
 * An incrementing and decrementing counter metric.
 *
 * 计数器型指标，适用于记录调用总量等类型的数据
 * </pre>
 */
public interface Counter extends Metric<Long> {

    void inc();

    void inc(long n);

    void dec();

    void dec(long n);

    long getCount();
}
