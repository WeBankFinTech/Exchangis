package com.webank.wedatasphere.exchangis.engine.resource.loader.datax;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Resource config for datax
 */
public class DataxEngineResourceConf {

    /**
     * Resource path prefix
     */
    public static final CommonVars<String> RESOURCE_PATH_PREFIX = CommonVars.apply("wds.exchangis.engine.datax.resource.path-prefix", "/datax/plugin");
}
