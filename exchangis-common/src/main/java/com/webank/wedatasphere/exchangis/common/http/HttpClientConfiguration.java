package com.webank.wedatasphere.exchangis.common.http;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Define the http configuration
 */
public class HttpClientConfiguration {

    /**
     * Connect timeout
     */
    public static final CommonVars<Integer> CONNECTION_TIMEOUT = CommonVars.apply("wds.exchangis.http.client.connection.timeout", 30000);

    /**
     * Connect timeout
     */
    public static final CommonVars<Integer> CONNECTION_REQUEST_TIMEOUT = CommonVars.apply("wds.exchangis.http.client.connection.request.timeout", 30000);

    /**
     * Read timeout
     */
    public static final CommonVars<Integer> SOCKET_READ_TIMEOUT = CommonVars.apply("wds.exchangis.http.client.read-timeout", 90000);

    /**
     * Max idle timeout
     */
    public static final CommonVars<Integer> MAX_IDLE_TIME = CommonVars.apply("wds.exchangis.http.client.max-idle-time", 30000);

    /**
     * Max connection size
     */
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.exchangis.http.client.connection.max-size", 100);


}
