package com.webank.wedatasphere.exchangis.common.linkis;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.Configuration;

/**
 * Configuration for linkis client
 */
public class ClientConfiguration {

    /**
     * Linkis server url
     */
    public static final CommonVars<String> LINKIS_SERVER_URL = CommonVars.apply("wds.exchangis.client.linkis.server-url", Configuration.getGateWayURL());

    /**
     * Linkis token value
     */
    public static final CommonVars<String> LINKIS_TOKEN_VALUE = CommonVars.apply("wds.exchangis.client.linkis.token.value", "EXCHANGIS-TOKEN");

}
