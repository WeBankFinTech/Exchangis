package com.webank.wedatasphere.exchangis.metrics.dto;

public class ExchangisTaskStatusMetricsDTO {

    private String status;

    private long num;

    public ExchangisTaskStatusMetricsDTO(String status, long num) {
        this.status = status;
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
