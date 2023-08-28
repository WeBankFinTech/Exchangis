package com.webank.wedatasphere.exchangis.engine.manager;

import com.webank.wedatasphere.exchangis.engine.ExchangisEngine;
import com.webank.wedatasphere.exchangis.engine.GenericExchangisEngine;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;

import java.util.HashMap;
import java.util.Map;


/**
 * Default Engine manager
 */
public class DefaultExchangisEngineManager implements ExchangisEngineManager {

    private static final ExchangisEngine EMPTY_ENGINE = new GenericExchangisEngine();
    /**
     * Engine context
     */
    protected Map<String, ExchangisEngine> engineContextMap = new HashMap<>();
    @Override
    public EngineSettings getSettings(String engine) {
        return engineContextMap.getOrDefault(engine, EMPTY_ENGINE).getSettings();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EngineResource, U extends EngineResource> EngineResourceContainer<T, U> getResourceContainer(String engine) {
        return (EngineResourceContainer<T, U>)
                engineContextMap.getOrDefault(engine, EMPTY_ENGINE).getResourceContainer();
    }
}
