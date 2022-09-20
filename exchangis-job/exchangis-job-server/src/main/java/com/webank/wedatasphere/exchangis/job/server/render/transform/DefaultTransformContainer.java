package com.webank.wedatasphere.exchangis.job.server.render.transform;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implement
 */
public class DefaultTransformContainer implements TransformerContainer {

    /**
     * Transforms
     */
    private final ConcurrentHashMap<String, Transformer> transforms = new ConcurrentHashMap<>();

    @Override
    public void registerTransformer(String type, Transformer transform) {
        this.transforms.put(type, transform);
    }

    @Override
    public Transformer getTransformer(String type) {
        return this.transforms.get(type);
    }
}
