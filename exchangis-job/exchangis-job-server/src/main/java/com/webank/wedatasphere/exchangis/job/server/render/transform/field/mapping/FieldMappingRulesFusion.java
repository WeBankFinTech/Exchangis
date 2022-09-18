package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategyFactory;

/**
 * Rules fusion
 */
public interface FieldMappingRulesFusion {
    /**
     * Fuse entrance
     * @param sourceRule source rule
     * @param sinkRule sink rule
     * @return combined rule
     */
    FieldMappingRule fuse(FieldMappingRule sourceRule, FieldMappingRule sinkRule);

    /**
     * Strategy factory
     * @return factory
     */
    FieldMatchStrategyFactory getFieldMatchStrategyFactory();
}
