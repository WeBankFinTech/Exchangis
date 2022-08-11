package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResException;

import java.util.Set;

/**
 * Engine resource path scanner
 */
public interface EngineResourcePathScanner {

    /**
     * Register resource loader
     * @param resourceLoader resource loader
     */
    void registerResourceLoader(EngineResourceLoader<? extends EngineLocalPathResource> resourceLoader);
    /**
     * Scan entrance
     * @param rootPath root path
     */
    Set<EngineLocalPathResource> doScan(String rootPath) throws ExchangisEngineResException;
}
