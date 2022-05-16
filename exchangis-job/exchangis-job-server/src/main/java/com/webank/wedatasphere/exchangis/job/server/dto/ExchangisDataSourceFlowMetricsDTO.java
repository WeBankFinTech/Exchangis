package com.webank.wedatasphere.exchangis.job.server.dto;

// 数据源流量指标对象
// datasource flow metrics pojo
public class ExchangisDataSourceFlowMetricsDTO {

    private String dataSourceTitle;

    private Long dataSourceId;

    // 监控的维度（秒、分钟、小时）
    private String dimension;

    private String flow;

    public String getDataSourceTitle() {
        return dataSourceTitle;
    }

    public void setDataSourceTitle(String dataSourceTitle) {
        this.dataSourceTitle = dataSourceTitle;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
}
