package com.webank.wedatasphere.exchangis.common.http;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Define the http configuration
 */
public class HttpClientConfiguration {

    /**
     * Connect timeout
     */
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.exchangis.http.client.connection.timeout", 30000L);

    /**
     * Max connection size
     */
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.exchangis.http.client.connection.max-size", 100);

    /**
     * Read timeout
     */
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.exchangis.http.client.read-timeout", 90000L);

}
