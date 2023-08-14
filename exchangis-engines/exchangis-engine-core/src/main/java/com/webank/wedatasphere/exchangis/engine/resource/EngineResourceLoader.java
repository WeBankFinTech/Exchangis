package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResLoadException;

import java.net.URI;

/**
 * Engine resource loader
 * @param <T>
 */
public interface EngineResourceLoader<T extends EngineResource> {
    /**
     * Engine type
     * @return engine
     */
    String engineType();
    /**
     * Accept uri
     * @param baseUri uri
     * @return boolean
     */
    boolean accept(URI baseUri, String path);

    /**
     * Load resources from uri
     * @param baseUri uri
     * @return resource array
     */
    T[] loadResource(URI baseUri, String path) throws ExchangisEngineResLoadException;
}
