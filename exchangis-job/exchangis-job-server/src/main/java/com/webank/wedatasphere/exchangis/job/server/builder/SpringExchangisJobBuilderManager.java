package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.job.builder.manager.DefaultExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.launcher.builder.LinkisExchangisLauncherJobBuilder;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.DataxExchangisEngineJobBuilder;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.DefaultExchangisEngineJobBuilder;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.SqoopExchangisEngineJobBuilder;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.GenericExchangisTransformJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder manager of spring context
 */
public class SpringExchangisJobBuilderManager  extends DefaultExchangisJobBuilderManager {

    private static final Logger LOG = LoggerFactory.getLogger(SpringExchangisJobBuilderManager.class);

    public void init(){
        //TransformJobBuilder
        GenericExchangisTransformJobBuilder transformJobBuilder = new GenericExchangisTransformJobBuilder();
        LOG.info("Init to load job handlers in [" + transformJobBuilder.getClass().getSimpleName() + "]");
        transformJobBuilder.initHandlers();
        //EngineJobBuilder
        DefaultExchangisEngineJobBuilder engineJobBuilder = new DefaultExchangisEngineJobBuilder();
        DataxExchangisEngineJobBuilder dataxExchangisEngineJobBuilder = new DataxExchangisEngineJobBuilder();
        SqoopExchangisEngineJobBuilder sqoopExchangisEngineJobBuilder = new SqoopExchangisEngineJobBuilder();
        //LaunchJobBuilder
        LinkisExchangisLauncherJobBuilder linkisExchangisLauncherJobBuilder = new LinkisExchangisLauncherJobBuilder();
        super.addJobBuilder(transformJobBuilder);
        super.addJobBuilder(engineJobBuilder);
        super.addJobBuilder(dataxExchangisEngineJobBuilder);
        super.addJobBuilder(sqoopExchangisEngineJobBuilder);
        super.addJobBuilder(linkisExchangisLauncherJobBuilder);
        super.initBuilderChains();
    }
}
