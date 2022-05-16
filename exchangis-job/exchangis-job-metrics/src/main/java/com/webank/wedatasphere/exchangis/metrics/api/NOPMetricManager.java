package com.webank.wedatasphere.exchangis.metrics.api;


import com.webank.wedatasphere.exchangis.metrics.Metric;

public class NOPMetricManager implements IMetricManager {
    @Override
    public Counter getCounter(MetricName name) {
        return NOP_COUNTER;
    }

    @Override
    public Counter getJdbcCounter(MetricName name) {
        return NOP_COUNTER;
    }

    @Override
    public void register(MetricName name, Metric<?> metric) {

    }

    public static final Counter NOP_COUNTER = new Counter() {
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

        @Override
        public long lastUpdateTime() {
            return 0;
        }

        @Override
        public MetricName getMetricName() {
            return MetricName.build("nop.counter");
        }
    };
}
