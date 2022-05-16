package com.webank.wedatasphere.exchangis.metrics.dto;

public class ExchangisTaskProcessMetricsDTO {

    private String key;

    private String title;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
