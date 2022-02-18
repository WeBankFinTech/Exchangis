package com.webank.wedatasphere.exchangis.job.server.dto;

/**
 *
 * @Date 2022/1/8 19:48
 */
public class ExchangisTaskIndicatorMetricsDTO {

    private Long exchangedRecords;

    private Long errorRecords;

    private Long ignoredRecords;

    public Long getExchangedRecords() {
        return exchangedRecords;
    }

    public void setExchangedRecords(Long exchangedRecords) {
        this.exchangedRecords = exchangedRecords;
    }

    public Long getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(Long errorRecords) {
        this.errorRecords = errorRecords;
    }

    public Long getIgnoredRecords() {
        return ignoredRecords;
    }

    public void setIgnoredRecords(Long ignoredRecords) {
        this.ignoredRecords = ignoredRecords;
    }
}
