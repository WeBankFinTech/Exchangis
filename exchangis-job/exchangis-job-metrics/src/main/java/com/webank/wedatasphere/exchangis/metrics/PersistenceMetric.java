package com.webank.wedatasphere.exchangis.metrics;

public interface PersistenceMetric extends Metric<String> {

    String getMetricSeq();

    String getMetricValueType();

}
