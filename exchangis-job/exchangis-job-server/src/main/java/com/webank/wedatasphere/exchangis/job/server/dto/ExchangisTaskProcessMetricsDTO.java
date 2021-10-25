package com.webank.wedatasphere.exchangis.job.server.dto;

public class ExchangisTaskProcessMetricsDTO {

    private Integer running;

    private Integer initialized;

    private Integer total;

    private String percentOfComplete;

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Integer getInitialized() {
        return initialized;
    }

    public void setInitialized(Integer initialized) {
        this.initialized = initialized;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getPercentOfComplete() {
        return percentOfComplete;
    }

    public void setPercentOfComplete(String percentOfComplete) {
        this.percentOfComplete = percentOfComplete;
    }
}
