package com.webank.wedatasphere.exchangis.common.linkis.client;

import com.webank.wedatasphere.exchangis.common.http.HttpClientConfiguration;
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

    /**
     * Linkis client max connections
     */
    public static final CommonVars<Integer> LINKIS_DEFAULT_MAX_CONNECTIONS = CommonVars.apply("wds.exchangis.client.linkis.max-connections.default",
            HttpClientConfiguration.MAX_CONNECTION_SIZE.getValue());


    /**
     * Linkis discovery enable
     */
    public static final CommonVars<Boolean> LINKIS_DISCOVERY_ENABLED = CommonVars.apply("wds.exchangis.client.linkis.discovery.enabled", true);

    /**
     * Linkis discovery frequency
     */
    public static final CommonVars<Long> LINKIS_DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.exchangis.client.linkis.discovery.frequency-period", 1L);

    /**
     * Linkis client load balance
     */
    public static final CommonVars<Boolean> LINKIS_LOAD_BALANCER_ENABLED = CommonVars.apply("wds.exchangis.client.linkis.load-balancer.enabled", true);


    /**
     * Linkis client retry
     */
    public static final CommonVars<Boolean> LINKIS_RETRY_ENABLED = CommonVars.apply("wds.exchangis.client.linkis.retry.enabled", false);

    /**
     * DWS version
     */
    public static final CommonVars<String> LINKIS_DWS_VERSION = CommonVars.apply("wds.exchangis.client.linkis.dws.version", Configuration.LINKIS_WEB_VERSION().getValue());
}
