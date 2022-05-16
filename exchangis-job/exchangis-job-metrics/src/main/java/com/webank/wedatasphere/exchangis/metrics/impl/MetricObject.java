package com.webank.wedatasphere.exchangis.metrics.impl;

import java.util.HashMap;
import java.util.Map;

public class MetricObject {

    private MetricObject() {

    }

    public static Builder named(String name) {
        return new Builder(name);
    }

    private String metric;

    private Long timestamp;

    private Object value;

    public static class Builder {

        private final MetricObject metric;

        public Builder(String name) {
            this.metric = new MetricObject();
            metric.metric = name;
        }

        public MetricObject build() {
            return metric;
        }

        public Builder withValue(Object value) {
            metric.value = value;
            return this;
        }

        public Builder withTimestamp(Long timestamp) {
            metric.timestamp = timestamp;
            return this;
        }
    }

}
