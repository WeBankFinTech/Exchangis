package com.webank.wedatasphere.exchangis.datasource.core;


import com.webank.wedatasphere.exchangis.common.linkis.client.ClientConfiguration;
import org.apache.linkis.common.conf.CommonVars;

/**
 * Exchangis data source config
 */
public class ExchangisDataSourceConfiguration {
    /**
     * Server url
     */
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.exchangis.datasource.client.server-url",
            ClientConfiguration.LINKIS_SERVER_URL.getValue());

    /**
     * Token value
     */
    public static final CommonVars<String> AUTH_TOKEN_VALUE = CommonVars.apply("wds.exchangis.datasource.client.token.value",
            ClientConfiguration.LINKIS_TOKEN_VALUE.getValue());

    /**
     * Dws version
     */
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.exchangis.datasource.client.dws.version",
            ClientConfiguration.LINKIS_DWS_VERSION.getValue());
}
