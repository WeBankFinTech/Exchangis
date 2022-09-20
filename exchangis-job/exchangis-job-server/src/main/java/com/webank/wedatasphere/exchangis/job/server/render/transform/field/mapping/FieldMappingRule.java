package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Field mapping rule
 */
public abstract class FieldMappingRule extends TransformRule {
    /**
     * Field add enable
     */
    protected boolean fieldAddEnable = true;

    /**
     * Field delete enable
     */
    protected boolean fieldDeleteEnable = true;

    /**
     * Field transform enable
     */
    protected boolean fieldTransformEnable = true;

    /**
     * Rule item of field edit enable
     */
    protected Map<String, Boolean> fieldEditEnableRuleItem = new HashMap<>();

    /**
     * Strategy name of field matching
     */
    protected String fieldMatchStrategyName;

    /**
     * Strategy of field matching
     */
    protected FieldMatchStrategy fieldMatchStrategy;

    /**
     * If ignore the unMatch field
     */
    protected boolean fieldUnMatchIgnore;

    public FieldMappingRule(){
        this.ruleType = TransformRule.Types.MAPPING.name();
    }

    public FieldMappingRule(TransformRule.Types type, String ruleSource) {
        super(type, ruleSource);
    }


    public boolean isFieldAddEnable() {
        return fieldAddEnable;
    }

    public void setFieldAddEnable(boolean fieldAddEnable) {
        this.fieldAddEnable = fieldAddEnable;
    }

    public boolean isFieldDeleteEnable() {
        return fieldDeleteEnable;
    }

    public void setFieldDeleteEnable(boolean fieldDeleteEnable) {
        this.fieldDeleteEnable = fieldDeleteEnable;
    }

    public boolean isFieldTransformEnable() {
        return fieldTransformEnable;
    }

    public void setFieldTransformEnable(boolean fieldTransformEnable) {
        this.fieldTransformEnable = fieldTransformEnable;
    }

    public Map<String, Boolean> getFieldEditEnableRuleItem() {
        return fieldEditEnableRuleItem;
    }

    public void setFieldEditEnableRuleItem(Map<String, Boolean> fieldEditEnableRuleItem) {
        this.fieldEditEnableRuleItem = fieldEditEnableRuleItem;
    }

    public String getFieldMatchStrategyName() {
        return fieldMatchStrategyName;
    }

    public void setFieldMatchStrategyName(String fieldMatchStrategyName) {
        this.fieldMatchStrategyName = fieldMatchStrategyName;
    }

    public FieldMatchStrategy getFieldMatchStrategy() {
        return fieldMatchStrategy;
    }

    public void setFieldMatchStrategy(FieldMatchStrategy fieldMatchStrategy) {
        this.fieldMatchStrategy = fieldMatchStrategy;
    }

    public boolean isFieldUnMatchIgnore() {
        return fieldUnMatchIgnore;
    }

    public void setFieldUnMatchIgnore(boolean fieldUnMatchIgnore) {
        this.fieldUnMatchIgnore = fieldUnMatchIgnore;
    }
}
