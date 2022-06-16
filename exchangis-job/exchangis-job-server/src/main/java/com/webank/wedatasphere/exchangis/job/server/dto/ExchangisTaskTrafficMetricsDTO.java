package com.webank.wedatasphere.exchangis.job.server.dto;

/**
 *
 * @Date 2022/1/8 19:45
 */
public class ExchangisTaskTrafficMetricsDTO {

    private String source;

    private String sink;

    private Long flow;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSink() {
        return sink;
    }

    public void setSink(String sink) {
        this.sink = sink;
    }

    public Long getFlow() {
        return flow;
    }

    public void setFlow(Long flow) {
        this.flow = flow;
    }
}
