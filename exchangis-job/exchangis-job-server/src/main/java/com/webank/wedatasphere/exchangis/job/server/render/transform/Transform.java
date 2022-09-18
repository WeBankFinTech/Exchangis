package com.webank.wedatasphere.exchangis.job.server.render.transform;

/**
 * Transform basic interface in rendering
 */
public interface Transform {
    /**
     * Get the settings by request params
     * @param requestVo request params
     * @return settings
     */
    TransformSettings getSettings(TransformRequestVo requestVo);
}
