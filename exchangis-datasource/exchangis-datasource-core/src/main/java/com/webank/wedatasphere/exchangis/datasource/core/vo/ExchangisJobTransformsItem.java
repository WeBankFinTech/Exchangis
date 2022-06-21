package com.webank.wedatasphere.exchangis.datasource.core.vo;

import java.util.List;

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
    @JsonProperty("deleteEnable")
    private boolean deleteEnable;

    @JsonProperty("source_field_index")
    private Integer sourceFieldIndex;

    @JsonProperty("sink_field_index")
    private Integer sinkFieldIndex;

    @JsonProperty("source_field_editable")
    private  boolean sourceFieldEditable;

    @JsonProperty("sink_field_editable")
    private boolean sinkFieldEditable;

    private List<String> validator;

    private ExchangisJobTransformer transformer;

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

    public List<String> getValidator() {
        return validator;
    }

    public void setValidator(List<String> validator) {
        this.validator = validator;
    }

    public ExchangisJobTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(ExchangisJobTransformer transformer) {
        this.transformer = transformer;
    }

    public Integer getSourceFieldIndex() {
        return sourceFieldIndex;
    }

    public void setSourceFieldIndex(Integer sourceFieldIndex) {
        this.sourceFieldIndex = sourceFieldIndex;
    }

    public Integer getSinkFieldIndex() {
        return sinkFieldIndex;
    }

    public void setSinkFieldIndex(Integer sinkFieldIndex) {
        this.sinkFieldIndex = sinkFieldIndex;
    }

    public boolean isDeleteEnable() {
        return deleteEnable;
    }

    public void setDeleteEnable(boolean deleteEnable) {
        this.deleteEnable = deleteEnable;
    }

    public boolean isSourceFieldEditable() {
        return sourceFieldEditable;
    }

    public void setSourceFieldEditable(boolean sourceFieldEditable) {
        this.sourceFieldEditable = sourceFieldEditable;
    }

    public boolean isSinkFieldEditable() {
        return sinkFieldEditable;
    }

    public void setSinkFieldEditable(boolean sinkFieldEditable) {
        this.sinkFieldEditable = sinkFieldEditable;
    }
}
