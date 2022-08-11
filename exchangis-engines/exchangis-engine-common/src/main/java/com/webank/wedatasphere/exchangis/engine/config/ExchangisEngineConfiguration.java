package com.webank.wedatasphere.exchangis.engine.config;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Engine configuration
 */
public class ExchangisEngineConfiguration {

    public static final CommonVars<String> ENGINE_RESOURCE_ROOT_PATH = CommonVars.apply("wds.exchangis.engine.root.path",
            System.getProperty("user.dir", "/tmp/exchangis/") + "/engine");
    /**
     * If need to store the merged resource into local path
     */
    public static final CommonVars<Boolean> ENGINE_RESOURCE_MERGE_LOCAL = CommonVars.apply("wds.exchangis.engine.resource.merge.local", true);

    public static final CommonVars<String> ENGINE_RESOURCE_TMP_PATH = CommonVars.apply("wds.exchangis.engine.resource.temp.path", "/tmp/exchangis/engine");

    /**
     * Packet suffix
     */
    public static final CommonVars<String> ENGINE_RESOURCE_PACKET_SUFFIX = CommonVars.apply("wds.exchangis.engine.resource.packet.suffix", ".zip");
}
