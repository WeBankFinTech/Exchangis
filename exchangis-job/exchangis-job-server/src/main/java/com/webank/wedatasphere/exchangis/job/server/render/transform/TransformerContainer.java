package com.webank.wedatasphere.exchangis.job.server.render.transform;

/**
 * Transform container
 */
public interface TransformerContainer {

    /**
     * Register transform
     * @param type type
     * @param transform transform
     */
    void registerTransformer(String type, Transformer transform);

    /**
     * Get transform
     * @param type type
     * @return transform
     */
    Transformer getTransformer(String type);
}
