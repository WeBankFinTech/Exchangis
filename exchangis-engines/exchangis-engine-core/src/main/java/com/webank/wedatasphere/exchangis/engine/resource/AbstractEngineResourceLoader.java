package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.resource.uri.ResourceURLStreamHandlerFactory;
import org.apache.linkis.common.conf.CommonVars;

import java.net.URL;

/**
 * Abstract resource loader
 * @param <T>
 */
public abstract class AbstractEngineResourceLoader<T extends EngineResource> implements EngineResourceLoader<T>{
    /**
     * Support schemes for uri
     */
    private static final CommonVars<String> SUPPORT_SCHEMES = CommonVars.apply("wds.exchangis.engine.resource.schemes", "bml,hdfs,viewfs");
    static{
        URL.setURLStreamHandlerFactory(new ResourceURLStreamHandlerFactory(
                SUPPORT_SCHEMES.getValue().split(",")));
    }
}
