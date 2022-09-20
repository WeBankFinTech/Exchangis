package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Rule entity
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldMappingRuleEntity extends FieldMappingRule{

    private String id;

    /**
     * Field edit enable
     */
    private boolean fieldEditEnable;

    /**
     * Field add enable
     */
    protected boolean fieldAddEnable = true;

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
    public FieldMappingRuleEntity(Types type, String ruleSource) {
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

}
