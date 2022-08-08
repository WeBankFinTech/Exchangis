package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;

/**
 * Engine resource uploader
 * @param <T>
 * @param <R>
 */
public interface EngineResourceUploader<T extends EngineResource, R extends EngineResource> {

    /**
     * upload method
     * @param needUploadResource resource need to be uploaded
     * @return uploaded resource
     */
    R upload(T needUploadResource);

    R upload(T needUploadResource, R relatedResource);
}
