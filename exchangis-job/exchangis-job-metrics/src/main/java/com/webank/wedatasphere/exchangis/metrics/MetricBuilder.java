package com.webank.wedatasphere.exchangis.metrics;

import com.webank.wedatasphere.exchangis.metrics.api.MetricName;

public interface MetricBuilder<T extends Metric<?>> {

    T build(MetricName name);

}
