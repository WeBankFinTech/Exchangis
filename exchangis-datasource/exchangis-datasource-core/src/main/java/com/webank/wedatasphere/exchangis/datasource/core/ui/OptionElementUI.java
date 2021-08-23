package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.Collection;

public class OptionElementUI implements ElementUI {
    private String key;
    private String field;
    private String label;
    private Collection<String> values;
    private String value;
    private Integer sort;
    private String unit;
    private Boolean required;

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
        return ElementUI.OPTION;
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
