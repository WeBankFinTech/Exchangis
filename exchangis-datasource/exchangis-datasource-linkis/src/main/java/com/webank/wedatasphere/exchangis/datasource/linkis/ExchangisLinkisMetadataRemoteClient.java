package com.webank.wedatasphere.exchangis.datasource.linkis;//package com.webank.wedatasphere.exchangis.datasource.linkis;
//
//import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
//import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
//import com.webank.wedatasphere.linkis.httpclient.config.ClientConfig;
//import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
//import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
//import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
//
//import java.util.concurrent.TimeUnit;
//
//public class ExchangisLinkisMetadataRemoteClient extends LinkisMetaDataRemoteClient {
//
//    public static final String serverUrl = ExchangisDataSourceConfiguration.SERVER_URL.getValue();
//
//    public static final Long connectionTimeout = ExchangisDataSourceConfiguration.CONNECTION_TIMEOUT.getValue();
//    public static final Boolean discoveryEnabled = ExchangisDataSourceConfiguration.DISCOVERY_ENABLED.getValue();
//    public static final Long discoveryFrequencyPeriod = ExchangisDataSourceConfiguration.DISCOVERY_FREQUENCY_PERIOD.getValue();
//    public static final Boolean loadbalancerEnabled = ExchangisDataSourceConfiguration.LOAD_BALANCER_ENABLED.getValue();
//    public static final Integer maxConnectionSize = ExchangisDataSourceConfiguration.MAX_CONNECTION_SIZE.getValue();
//    public static final Boolean retryEnabled = ExchangisDataSourceConfiguration.RETRY_ENABLED.getValue();
//    public static final Long readTimeout = ExchangisDataSourceConfiguration.READ_TIMEOUT.getValue();
//    public static final String authTokenKey = ExchangisDataSourceConfiguration.AUTHTOKEN_KEY.getValue();
//    public static final String authTokenValue = ExchangisDataSourceConfiguration.AUTHTOKEN_VALUE.getValue();
//    public static final String dwsVersion = ExchangisDataSourceConfiguration.DWS_VERSION.getValue();
//
//    private static volatile ExchangisLinkisMetadataRemoteClient instance = null;
//    private static final LinkisMetaDataRemoteClient dataSourceClient;
//    static {
//        ClientConfig clientConfig = DWSClientConfigBuilder.newBuilder()
//                .addServerUrl(serverUrl)
//                .connectionTimeout(30000L)
//                .discoveryEnabled(false)
//                .discoveryFrequency(1, TimeUnit.MINUTES)
//                .loadbalancerEnabled(true)
//                .maxConnectionSize(5)
//                .retryEnabled(false)
//                .readTimeout(30000L)
//                .setAuthenticationStrategy(new StaticAuthenticationStrategy())
//                .setAuthTokenKey(authTokenKey)
//                .setAuthTokenValue(authTokenValue)
////                .setDWSVersion("v1")
//                .build();
//
//        dataSourceClient = new LinkisMetaDataRemoteClient((DWSClientConfig) clientConfig);
//    }
//
//    private ExchangisLinkisMetadataRemoteClient() {
//        super();
//    }
//
//    public static ExchangisLinkisMetadataRemoteClient getInstance() {
//        if (null == instance) {
//            synchronized (ExchangisLinkisMetadataRemoteClient.class) {
//                if (null == instance) {
//                    instance = new ExchangisLinkisMetadataRemoteClient();
//                }
//            }
//        }
//        return instance;
//    }
//}
