package com.webank.wedatasphere.exchangis.job.server.vo;

import com.webank.wedatasphere.exchangis.job.server.metrics.ExchangisMetricsVo;

/**
 *
 * @Date 2022/1/12 23:00
 */
public class ExchangisLaunchedTaskMetricsVo {
    private String taskId;

    private String name;

    private String status;

    private ExchangisMetricsVo metrics;
    //private Map<String, Object> metrics;

    public ExchangisLaunchedTaskMetricsVo(){

    }

    public ExchangisLaunchedTaskMetricsVo(String taskId, String name, String status, ExchangisMetricsVo metrics){
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.metrics = metrics;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
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

    public ExchangisMetricsVo getMetrics() {
        return metrics;
    }

    public void setMetrics(ExchangisMetricsVo metrics) {
        this.metrics = metrics;
    }
}
