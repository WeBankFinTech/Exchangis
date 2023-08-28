package com.webank.wedatasphere.exchangis.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("exchangis_job_param_config")
public class ExchangisJobParamConfig {
    public static final String DIRECTION_SOURCE = "SOURCE";
    public static final String DIRECTION_SINK = "SINK";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "config_key")
    private String configKey;

    @TableField(value = "config_name")
    private String configName;

    @TableField(value = "config_direction")
    private String configDirection;

    private String type;

    @TableField(value = "ui_type")
    private String uiType;

    @TableField(value = "ui_field")
    private String uiField;

    @TableField(value = "ui_label")
    private String uiLabel;

    @TableField(value = "unit")
    private String unit;

    @TableField(value = "required")
    private Boolean required;

    @TableField(value = "value_type")
    private String valueType;

    @TableField(value = "value_range")
    private String valueRange;

    @TableField(value = "default_value")
    private String defaultValue;

    @TableField(value = "validate_type")
    private String validateType;

    @TableField(value = "validate_range")
    private String validateRange;

    @TableField(value = "validate_msg")
    private String validateMsg;

    @TableField(value = "is_hidden")
    private Boolean hidden;

    @TableField(value = "is_advanced")
    private Boolean advanced;

    @TableField(value = "ref_id")
    private Long refId;

    /**
     * store url exa. http://127.0.0.1/api/v1/dss/exchangis/main/xxx
     */
    private String source;

    private Integer level;

    private String treename;

    private Integer sort;

    private String description;

    private Integer status;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public String getUiField() {
        return uiField;
    }

    public void setUiField(String uiField) {
        this.uiField = uiField;
    }

    public String getUiLabel() {
        return uiLabel;
    }

    public void setUiLabel(String uiLabel) {
        this.uiLabel = uiLabel;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValueRange() {
        return valueRange;
    }

    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getAdvanced() {
        return advanced;
    }

    public void setAdvanced(Boolean advanced) {
        this.advanced = advanced;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTreename() {
        return treename;
    }

    public void setTreename(String treename) {
        this.treename = treename;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getConfigDirection() {
        return configDirection;
    }

    public void setConfigDirection(String configDirection) {
        this.configDirection = configDirection;
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

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }
}