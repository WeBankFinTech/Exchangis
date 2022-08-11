package com.webank.wedatasphere.exchangis.engine.dao;

import com.webank.wedatasphere.exchangis.engine.domain.EngineStoreResource;

import java.util.List;

/**
 * Engine resource dao
 */
public interface EngineResourceDao {

    /**
     * Get resources by engine type
     * @param engineType engine type
     * @return store resource
     */
    List<EngineStoreResource> getResources(String engineType);

    /**
     * Insert Resource
     * @param storeResource store resource
     */
    void insertResource(EngineStoreResource storeResource);

    /**
     * Update resource
     * @param storeResource store resource
     */
    void updateResource(EngineStoreResource storeResource);
}
