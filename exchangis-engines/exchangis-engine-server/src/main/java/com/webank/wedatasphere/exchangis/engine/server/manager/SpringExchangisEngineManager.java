package com.webank.wedatasphere.exchangis.engine.server.manager;

import com.webank.wedatasphere.exchangis.engine.ExchangisEngine;
import com.webank.wedatasphere.exchangis.engine.GenericExchangisEngine;
import com.webank.wedatasphere.exchangis.engine.dao.EngineResourceDao;
import com.webank.wedatasphere.exchangis.engine.dao.EngineSettingsDao;
import com.webank.wedatasphere.exchangis.engine.domain.EngineBmlResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResException;
import com.webank.wedatasphere.exchangis.engine.manager.DefaultExchangisEngineManager;
import com.webank.wedatasphere.exchangis.engine.resource.DefaultEngineResourceContainer;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourcePathScanner;
import com.webank.wedatasphere.exchangis.engine.resource.bml.BmlEngineResourceUploader;
import com.webank.wedatasphere.exchangis.engine.resource.loader.AbstractEngineLocalPathResourceLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Engine manager of spring context
 */
public class SpringExchangisEngineManager extends DefaultExchangisEngineManager {

    private static final Logger LOG = LoggerFactory.getLogger(SpringExchangisEngineManager.class);

    /**
     * Resource dao
     */
    private final EngineResourceDao resourceDao;

    /**
     * Settings dao
     */
    private final EngineSettingsDao settingsDao;

    /**
     * Resource root path
     */
    private final String rootPath;

    private final EngineResourcePathScanner scanner;

    public SpringExchangisEngineManager(String rootPath, EngineResourceDao resourceDao,
                                        EngineSettingsDao settingsDao, EngineResourcePathScanner scanner){
        this.rootPath = rootPath;
        this.resourceDao = resourceDao;
        this.settingsDao = settingsDao;
        this.scanner = scanner;
    }

    public void init(){
        List<EngineSettings> settingsList = this.settingsDao.getSettings();
        try {
            String settingsJson = JsonUtils.jackson().writer().writeValueAsString(settingsList);
            LOG.info("Engine settings: {}", settingsJson);
        }catch(Exception e){
            //Ignore
        }
        settingsList.forEach(settings -> {
            GenericExchangisEngine engine = new GenericExchangisEngine();
            engine.setSettings(settings);
            AbstractEngineLocalPathResourceLoader loader = null;
            BmlEngineResourceUploader uploader = null;
            String loaderClassName = settings.getResourceLoaderClass();
            if (StringUtils.isNotBlank(loaderClassName)){
                try {
                    Class<?> loaderClass = Class.forName(loaderClassName);
                    if (AbstractEngineLocalPathResourceLoader.class.isAssignableFrom(loaderClass)){
                        loader = (AbstractEngineLocalPathResourceLoader) loaderClass.newInstance();
                        this.scanner.registerResourceLoader(loader);
                    } else {
                        LOG.warn("Not allow the loader class: '{}' which does not implement '{}'", loaderClass, AbstractEngineLocalPathResourceLoader.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    LOG.warn("Cannot find the loader class: '{}' for engine [{}]", loaderClassName, engine.getName());
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.warn("Fail to instantiate the loader class: '{}'", loaderClassName, e);
                }
            }
            String uploaderClassName = Optional.ofNullable(settings.getResourceUploaderClass())
                    .orElse(BmlEngineResourceUploader.class.getCanonicalName());
            try {
                Class<?> uploaderClass = Class.forName(uploaderClassName);
                if (BmlEngineResourceUploader.class.isAssignableFrom(uploaderClass)){
                    uploader = (BmlEngineResourceUploader) uploaderClass.newInstance();
                } else {
                    LOG.warn("Not allow the uploader class: '{}' which does not implement '{}'", uploaderClass,
                            BmlEngineResourceUploader.class.getName());
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Cannot find the uploader class: '{}' for engine [{}]", uploaderClassName, engine.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.warn("Fail to instantiate the uploader class: '{}'", uploaderClassName, e);
            }
            EngineResourceContainer<EngineLocalPathResource, EngineBmlResource>
                resourceContainer = new DefaultEngineResourceContainer(engine.getName(), rootPath, resourceDao, loader, uploader);
            LOG.info("Init engine resource container for engine: [{}]", engine.getName());
            resourceContainer.init();
            engine.setResourceContainer(resourceContainer);
            engineContextMap.put(engine.getName(), engine);
        });
        try {
            // Start to scan and load local resources
            Set<EngineLocalPathResource> localResources = this.scanner.doScan(this.rootPath);
            localResources.forEach(resource -> Optional.ofNullable(engineContextMap.get(resource.getEngineType()))
                    .ifPresent(engine -> engine.getResourceContainer().addResource(resource.getPath(), resource)));
        }catch (ExchangisEngineResException e){
            LOG.warn("Exception happened when scanning root path: [{}]", this.rootPath, e);
        }
        LOG.info("Flush all the resources in engine resource containers");
        for(Map.Entry<String, ExchangisEngine> entry : engineContextMap.entrySet()){
            try {
                entry.getValue().getResourceContainer().flushAllResources();
            } catch (ExchangisEngineResException e) {
                LOG.warn("Unable to flush the resources in container for engine: [{}]", entry.getValue().getName(), e);
            }
        }
    }
}
