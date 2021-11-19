package com.webank.wedatasphere.exchangis.datasource.linkis;//package com.webank.wedatasphere.exchangis.datasource.linkis;
//
//import com.webank.wedatasphere.linkis.datasource.client.DataSourceRemoteClient;
//import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
//import com.webank.wedatasphere.linkis.datasource.client.request.GetAllDataSourceTypesAction;
//import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
//import com.webank.wedatasphere.linkis.httpclient.config.ClientConfig;
//import com.webank.wedatasphere.linkis.httpclient.config.ClientConfigBuilder;
//import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
//import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
//import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class ExchangisLinkisDataSourceRemoteClient extends LinkisDataSourceRemoteClient {
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
//    private static volatile ExchangisLinkisDataSourceRemoteClient instance = null;
////    private static final LinkisDataSourceRemoteClient dataSourceClient;
//
//    private final DWSClientConfig clientConfig = (DWSClientConfig) DWSClientConfigBuilder.newBuilder()
//            .addServerUrl(serverUrl)
//            .connectionTimeout(30000L)
//            .discoveryEnabled(false)
//            .discoveryFrequency(1, TimeUnit.MINUTES)
//            .loadbalancerEnabled(true)
//            .maxConnectionSize(5)
//            .retryEnabled(false)
//            .readTimeout(30000L)
//            .setAuthenticationStrategy(new StaticAuthenticationStrategy())
//            .setAuthTokenKey(authTokenKey)
//            .setAuthTokenValue(authTokenValue)
////                .setDWSVersion("v1")
//            .build();
////    static {
////        ClientConfig clientConfig = DWSClientConfigBuilder.newBuilder()
////                .addServerUrl(serverUrl)
////                .connectionTimeout(30000L)
////                .discoveryEnabled(false)
////                .discoveryFrequency(1, TimeUnit.MINUTES)
////                .loadbalancerEnabled(true)
////                .maxConnectionSize(5)
////                .retryEnabled(false)
////                .readTimeout(30000L)
////                .setAuthenticationStrategy(new StaticAuthenticationStrategy())
////                .setAuthTokenKey(authTokenKey)
////                .setAuthTokenValue(authTokenValue)
//////                .setDWSVersion("v1")
////                .build();
////
////        dataSourceClient = new LinkisDataSourceRemoteClient((DWSClientConfig) clientConfig);
////    }
//
//    private ExchangisLinkisDataSourceRemoteClient() {
//        super(clientConfig);
////        super(clientConfig);
//
//
//    }
//
//    public static ExchangisLinkisDataSourceRemoteClient getInstance() {
//        if (null == instance) {
//            synchronized (ExchangisLinkisDataSourceRemoteClient.class) {
//                if (null == instance) {
//                    instance = new ExchangisLinkisDataSourceRemoteClient();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public List<DataSourceType> queryDataSourceTypes(String user) {
//
//        return dataSourceClient.getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
//                .setUser(user)
//                .build()
//        ).getAllDataSourceType();
//    }
//}
