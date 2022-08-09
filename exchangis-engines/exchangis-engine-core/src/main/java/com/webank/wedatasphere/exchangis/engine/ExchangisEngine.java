package com.webank.wedatasphere.exchangis.engine;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;

/**
 * Exchangis engine
 */
public interface ExchangisEngine {

    /**
     * engine name
     * @return
     */
    String getName();

    /**
     * Settings
     * @return settings
     */
    EngineSettings getSettings();

    /**
     * Resource container
     * @return container
     */
    <T extends EngineResource, U extends EngineResource> EngineResourceContainer<T, U> getResourceContainer();
}
