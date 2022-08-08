package com.webank.wedatasphere.exchangis.engine.manager;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;

/**
 * Engine manager
 */
public interface ExchangisEngineManager {

    /**
     *
     * @param engine engine
     * @return
     */
    EngineSettings getSettings(String engine);

    /**
     * Get engine resource container
     * @param engine engine
     * @param <T>
     * @param <U>
     * @return
     */
    <T extends EngineResource, U extends EngineResource> EngineResourceContainer<T, U> getResourceContainer(String engine);
}
