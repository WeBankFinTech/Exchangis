package com.webank.wedatasphere.exchangis.datasource.core.ui;

public class MapElementUI implements ElementUI<String> {
    private String key;
    private String field;
    private String label;
    private Integer sort;
    private String value;
    private String defaultValue;
    private String unit;
    private Boolean required;
    private String validateType;
    private String validateRange;
    private String validateMsg;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getType() {
        return ElementUI.MAP;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getDefaultValue() { return defaultValue; }

    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

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

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public String getValidateRange() {
        return validateRange;
    }

    public void setValidateRange(String validateRange) {
        this.validateRange = validateRange;
    }

    public String getValidateMsg() { return validateMsg; }

    public void setValidateMsg(String validateMsg) { this.validateMsg = validateMsg; }
}
