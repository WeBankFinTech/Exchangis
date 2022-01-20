package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskMetricsDTO;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/1/12 23:00
 */
public class ExchangisLaunchedTaskMetricsVO {
    private Long taskId;

    private String name;

    private String status;

    private Map<String, Object> metrics;

    public ExchangisLaunchedTaskMetricsVO(Long taskId, String name, String status, Map<String, Object> metrics){
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.metrics = metrics;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
}
