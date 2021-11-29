package com.webank.wedatasphere.exchangis.metrics;

import com.webank.wedatasphere.exchangis.metrics.api.MetricName;

import java.util.Date;

public interface Metric<T> {

//    String getMetricTitle();
//
//    String getMetricNorm();
//
//    T getMetricValue();
//
//    Date getMetricTime();

    long lastUpdateTime();

    MetricName getMetricName();

}
