package com.webank.wedatasphere.exchangis.engine;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;

import java.util.Objects;

/**
 * Generic implement
 */
public class GenericExchangisEngine implements ExchangisEngine{

    @Override
    public String getName() {
        return Objects.nonNull(settings) ? settings.getName(): null;
    }

    /**
     * Settings
     */
    private EngineSettings settings;

    /**
     * Resource container
     */
    private EngineResourceContainer<?, ?> resourceContainer;

    public EngineSettings getSettings() {
        return settings;
    }

    public void setSettings(EngineSettings settings) {
        this.settings = settings;
    }

    @SuppressWarnings("unchecked")
    public <T extends EngineResource, U extends EngineResource> EngineResourceContainer<T, U> getResourceContainer() {
        return (EngineResourceContainer<T, U>) resourceContainer;
    }

    public void setResourceContainer(EngineResourceContainer<?, ?> resourceContainer) {
        this.resourceContainer = resourceContainer;
    }

}
