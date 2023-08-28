package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.engine.domain.EngineBmlResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.manager.ExchangisEngineManager;
import com.webank.wedatasphere.exchangis.engine.resource.EngineResourceContainer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.builder.AbstractLoggingExchangisJobBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Engine job builder with engine resources
 */
public abstract class AbstractResourceEngineJobBuilder extends AbstractLoggingExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    /**
     * Get resources
     * @return path
     */
    protected List<EngineResource> getResources(String engine, String[] paths){
        EngineResourceContainer<EngineLocalPathResource, EngineBmlResource> resourceContainer =
                getBean(ExchangisEngineManager.class).getResourceContainer(engine);
        List<EngineResource> resources = new ArrayList<>();
        if (Objects.nonNull(resourceContainer)){
            for(String path : paths){
                if (StringUtils.isNotBlank(path)) {
                    Optional.ofNullable(resourceContainer.getRemoteResource(path))
                            .ifPresent(resources::add);
                }
            }
        }
        return resources;
    }
}
