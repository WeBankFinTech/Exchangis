package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

/**
 * Field match strategy factory
 */
public interface FieldMatchStrategyFactory {
    /**
     *
     * @param strategyName strategy
     * @return
     */
    FieldMatchStrategy getOrCreateStrategy(String strategyName);
}
