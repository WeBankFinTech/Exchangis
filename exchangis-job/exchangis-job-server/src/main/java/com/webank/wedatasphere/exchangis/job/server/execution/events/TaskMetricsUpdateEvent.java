package com.webank.wedatasphere.exchangis.job.server.execution.events;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchedExchangisTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Updating of task info
 */
public class TaskMetricsUpdateEvent extends TaskExecutionEvent{

    private Map<String, Object> metrics = new HashMap<>();

    public TaskMetricsUpdateEvent(LaunchedExchangisTask task, Map<String, Object> metrics) {
        super(task);
        this.metrics = metrics;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
}
