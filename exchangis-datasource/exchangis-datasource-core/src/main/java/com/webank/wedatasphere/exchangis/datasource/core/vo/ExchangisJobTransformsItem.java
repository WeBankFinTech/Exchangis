package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangisJobTransformsItem {
    @JsonProperty("source_field_name")
    private String sourceFieldName;
    @JsonProperty("source_field_type")
    private String sourceFieldType;
    @JsonProperty("sink_field_name")
    private String sinkFieldName;
    @JsonProperty("sink_field_type")
    private String sinkFieldType;

    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public String getSourceFieldType() {
        return sourceFieldType;
    }

    public void setSourceFieldType(String sourceFieldType) {
        this.sourceFieldType = sourceFieldType;
    }

    public String getSinkFieldName() {
        return sinkFieldName;
    }

    public void setSinkFieldName(String sinkFieldName) {
        this.sinkFieldName = sinkFieldName;
    }

    public String getSinkFieldType() {
        return sinkFieldType;
    }

    public void setSinkFieldType(String sinkFieldType) {
        this.sinkFieldType = sinkFieldType;
    }
}
