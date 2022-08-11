package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResException;

import java.util.List;

/**
 * Resource container
 */
public interface EngineResourceContainer<T extends EngineResource, U extends EngineResource> {

    /**
     * Engine type related
     * @return string
     */
    String getEngineType();

    /**
     * Init method
     */
    void init();
    /**
     * Get existed engine resources from resource path
     * @param resourcePath resource path
     * @return engine resources
     */
    List<T> getResources(String resourcePath);

    /**
     * Add resource to path in container
     * @param resourcePath resource path
     * @param engineResource engine resource
     */
    void addResource(String resourcePath, T engineResource);

    /**
     * Update resource
     * @param resourcePath resource path
     * @param engineResource engine resource
     * @return resource list
     */
    void updateResources(String resourcePath, T[] engineResource);

    /**
     * Get resource by path and id
     * @param resourcePath resource path
     * @param resourceId resource id
     * @return engine
     */
    T getResource(String resourcePath, String resourceId);

    /**
     * Flush(upload) resources in path
     * @param resourcePath resource path
     * @return (merged)resource
     */
    void flushResources(String resourcePath) throws ExchangisEngineResException;

    /**
     * Flush(upload) all the resources in container
     */
    void flushAllResources() throws ExchangisEngineResException;

    /**
     * Get remote(upload) resource in path
     * @param resourcePath resource path
     * @return (merged)resource
     */
    U getRemoteResource(String resourcePath);
    /**
     * Remove resource
     * @param resourcePath resource path
     * @param resourceId resource id
     */
    void removeResource(String resourcePath, String resourceId);

    /**
     * Engine resource loader
     * @return scanner
     */
    EngineResourceLoader<T>  getResourceLoader();

    /**
     * Engine resource uploader
     * @return resource uploader
     */
    EngineResourceUploader<T, U> getResourceUploader();
}
