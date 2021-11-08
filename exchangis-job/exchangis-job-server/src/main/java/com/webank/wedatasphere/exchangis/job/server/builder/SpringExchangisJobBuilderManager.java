package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.job.builder.manager.DefaultExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.GenericExchangisTransformJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Builder manager of spring context
 */
@Component
public class SpringExchangisJobBuilderManager  extends DefaultExchangisJobBuilderManager {

    private static final Logger LOG = LoggerFactory.getLogger(SpringExchangisJobBuilderManager.class);
    @PostConstruct
    public void init(){
        GenericExchangisTransformJobBuilder transformJobBuilder = new GenericExchangisTransformJobBuilder();
        LOG.info("Init to load job handlers in [" + transformJobBuilder.getClass().getSimpleName() + "]");
        transformJobBuilder.initHandlers();
        super.addJobBuilder(transformJobBuilder);
        super.initBuilderChains();
    }
}
