package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangisJobDataSourcesContent {

    // 唯一标识符
    // HIVE.ID.DB.TABLE
    @JsonProperty("source_id")
    private String sourceId;

    @JsonProperty("sink_id")
    private String sinkId;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSinkId() {
        return sinkId;
    }

    public void setSinkId(String sinkId) {
        this.sinkId = sinkId;
    }
}
