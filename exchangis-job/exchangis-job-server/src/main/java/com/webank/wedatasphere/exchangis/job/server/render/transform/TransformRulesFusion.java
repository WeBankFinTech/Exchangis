package com.webank.wedatasphere.exchangis.job.server.render.transform;


/**
 * Transform rule fusion
 * @param <T>
 */
public interface TransformRulesFusion<T extends TransformRule> {
    /**
     * Fuse entrance
     * @param sourceRule source rule
     * @param sinkRule sink rule
     * @return fused rule
     */
    T fuse(T sourceRule, T sinkRule);

}
