package com.webank.wedatasphere.exchangis.metrics.impl;

import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricName;

public interface Collector {

    void collect(MetricName name, Counter counter, long timestamp);

}
