package com.webank.wedatasphere.exchangis.datasource.core.ui;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;

import java.util.Collection;
import java.util.Map;

public class OptionElementUI implements ElementUI<String> {
    private Long id;
    private String key;
    private String field;
    private String label;
    private Collection<String> values;
    private String value;
    private String defaultValue;
    private Integer sort;
    private String unit;
    private Boolean required;
    private Long refId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getField() {
        return field;
    }

    @Override
    public String getType() {
        return Type.OPTION.name();
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getDefaultValue() { return defaultValue; }

    @Override
    public void setValue(Map<String, Object> params) {
        this.value = Json.toJson(params.values(), null);
    }

    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    @Override
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
