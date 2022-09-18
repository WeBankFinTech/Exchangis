package com.webank.wedatasphere.exchangis.job.server.render.transform;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implement
 */
public class DefaultTransformContainer implements TransformContainer{

    /**
     * Transforms
     */
    private ConcurrentHashMap<String, Transform> transforms = new ConcurrentHashMap<>();

    @Override
    public void registerTransform(String type, Transform transform) {
        this.transforms.put(type, transform);
    }

    @Override
    public Transform getTransform(String type) {
        return this.transforms.get(type);
    }
}
