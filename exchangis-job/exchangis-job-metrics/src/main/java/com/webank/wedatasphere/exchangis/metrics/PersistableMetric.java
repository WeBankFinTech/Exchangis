package com.webank.wedatasphere.exchangis.metrics;

public interface PersistableMetric extends PersistenceMetric {

    PersistableMetric toPersistenceMetric();

}
