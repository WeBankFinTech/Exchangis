package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Rule entity
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldMappingRuleEntity extends FieldMappingRule{

    static {
        // Register type
        TransformRule.typeClasses.put(TransformRule.Types.MAPPING.name(), FieldMappingRuleEntity.class);
    }

    private String id;

    /**
     * Field edit enable
     */
    private boolean fieldEditEnable = true;

    @Override
    public Map<String, Boolean> getFieldEditEnableRuleItem() {
        Map<String, Boolean> ruleItem = super.getFieldEditEnableRuleItem();
        Optional.ofNullable(ruleItem).ifPresent(item -> {
            if (StringUtils.isNotBlank(direction)){
                item.computeIfAbsent(direction,
                        name -> fieldEditEnable);
            }
        });
        return ruleItem;
    }

    public FieldMappingRuleEntity(){
        super();
    }
    public FieldMappingRuleEntity(TransformRule.Types type, String ruleSource) {
        super(type, ruleSource);
        if (StringUtils.isNotBlank(ruleSource)){
            FieldMappingRuleEntity ruleEntity = Json.fromJson(ruleSource, FieldMappingRuleEntity.class);
            Optional.ofNullable(ruleEntity).ifPresent(rule -> {
                this.fieldAddEnable = rule.fieldAddEnable;
                this.fieldDeleteEnable = rule.fieldDeleteEnable;
                this.fieldTransformEnable = rule.fieldTransformEnable;
                this.fieldMatchStrategyName = rule.fieldMatchStrategyName;
                this.fieldEditEnable = rule.fieldEditEnable;
            });
        }
    }

    public boolean isFieldEditEnable() {
        return fieldEditEnable;
    }

    public void setFieldEditEnable(boolean fieldEditEnable) {
        this.fieldEditEnable = fieldEditEnable;
    }

}
