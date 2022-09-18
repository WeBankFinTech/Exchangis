package com.webank.wedatasphere.exchangis.job.server.render.transform;

/**
 * Transform container
 */
public interface TransformContainer {

    /**
     * Register transform
     * @param type type
     * @param transform transform
     */
    void registerTransform(String type, Transform transform);

    /**
     * Get transform
     * @param type type
     * @return transform
     */
    Transform getTransform(String type);
}
