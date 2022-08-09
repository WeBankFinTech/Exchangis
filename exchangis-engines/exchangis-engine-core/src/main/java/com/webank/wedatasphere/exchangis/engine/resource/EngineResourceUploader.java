package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResUploadException;

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
    R upload(T needUploadResource) throws ExchangisEngineResUploadException;

    R upload(T needUploadResource, R relatedResource) throws ExchangisEngineResUploadException;
}
