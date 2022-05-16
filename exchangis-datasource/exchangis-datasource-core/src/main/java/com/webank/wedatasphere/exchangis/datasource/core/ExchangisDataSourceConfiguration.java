package com.webank.wedatasphere.exchangis.datasource.core;


import org.apache.linkis.common.conf.CommonVars;

public class ExchangisDataSourceConfiguration {
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.exchangis.datasource.client.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.exchangis.datasource.client.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.exchangis.datasource.client.discovery.enabled", true);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.exchangis.datasource.client.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.exchangis.datasource.client.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.exchangis.datasource.client.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.exchangis.datasource.client.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.exchangis.datasource.client.readtimeout", 30000L);

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.exchangis.datasource.client.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.exchangis.datasource.client.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.exchangis.datasource.client.dws.version", "");
}
