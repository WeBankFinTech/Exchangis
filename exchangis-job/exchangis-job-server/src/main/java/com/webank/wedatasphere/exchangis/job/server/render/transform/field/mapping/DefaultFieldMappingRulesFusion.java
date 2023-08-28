package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategyFactory;
import org.apache.commons.lang.StringUtils;

/**
 * Rules fusion
 */
public class DefaultFieldMappingRulesFusion implements FieldMappingRulesFusion{

    /**
     * Strategy factory
     */
    private final FieldMatchStrategyFactory matchStrategyFactory;

    public DefaultFieldMappingRulesFusion(FieldMatchStrategyFactory matchStrategyFactory){
        this.matchStrategyFactory = matchStrategyFactory;
    }

    @Override
    public FieldMappingRule fuse(FieldMappingRule sourceRule, FieldMappingRule sinkRule) {
        FieldMappingRule fusedRule = new FieldMappingRuleEntity();
        // And calculate
        fusedRule.setFieldAddEnable(sourceRule.fieldAddEnable & sinkRule.fieldAddEnable);
        fusedRule.setFieldDeleteEnable(sourceRule.fieldDeleteEnable & sinkRule.fieldDeleteEnable);
        fusedRule.setFieldTransformEnable(sourceRule.fieldTransformEnable & sinkRule.fieldTransformEnable);
        // Combine calculate
        fusedRule.getFieldEditEnableRuleItem().putAll(sourceRule.getFieldEditEnableRuleItem());
        fusedRule.getFieldEditEnableRuleItem().putAll(sinkRule.getFieldEditEnableRuleItem());
        // Prior to choose sink rule
        String matchStrategy = sinkRule.getFieldMatchStrategyName();
        TransformRule.Direction matchDirection = TransformRule.Direction.SINK;
        if (StringUtils.isBlank(matchStrategy)){
            matchStrategy = sourceRule.getFieldMatchStrategyName();
            if (StringUtils.isNotBlank(matchStrategy)){
                matchDirection = TransformRule.Direction.SOURCE;
            }
        }
        fusedRule.setFieldMatchStrategyName(matchStrategy);
        if (StringUtils.isNotBlank(matchStrategy)){
            fusedRule.setFieldMatchStrategy(getFieldMatchStrategyFactory().getOrCreateStrategy(matchStrategy));
        }
        fusedRule.setDirection(matchDirection.name());
        fusedRule.setFieldUnMatchIgnore( matchDirection == TransformRule.Direction.SOURCE ?
                sourceRule.fieldUnMatchIgnore : sinkRule.fieldUnMatchIgnore);
        return fusedRule;
    }

    @Override
    public FieldMatchStrategyFactory getFieldMatchStrategyFactory() {
        return this.matchStrategyFactory;
    }
}
