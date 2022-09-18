package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Field mapping rule
 */
public class FieldMappingRule extends TransformRule {

    static {
        // Register type
        TransformRule.typeClasses.put(TransformRule.Types.MAPPING.name(), FieldMappingRule.class);
    }
    /**
     * Field add enable
     */
    protected boolean fieldAddEnable;

    /**
     * Field delete enable
     */
    protected boolean fieldDeleteEnable;

    /**
     * Field transform enable
     */
    protected boolean fieldTransformEnable;

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

    public FieldMappingRule(){
        this.type = TransformRule.Types.MAPPING.name();
    }

    public FieldMappingRule(Types type, String ruleSource) {
        super(type, ruleSource);
    }

    public enum Storage {
        DEF_SOURCE, DEF_SINK
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
}
