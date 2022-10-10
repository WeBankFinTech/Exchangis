package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRulesFusion;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategyFactory;

/**
 * Rules fusion
 */
public interface FieldMappingRulesFusion extends TransformRulesFusion<FieldMappingRule> {

    /**
     * Strategy factory
     * @return factory
     */
    FieldMatchStrategyFactory getFieldMatchStrategyFactory();
}
