package com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6;

import com.alibaba.datax.common.exception.DataXException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Classname ElasticRestClient
 * @Description TODO
 * @Date 2021/1/3 11:18
 * @Created by limeng
 */
public class ElasticRestClient {
    private static final Logger log = LoggerFactory.getLogger(ElasticRestClient.class);

    static RestClient createClient(String[] endPoints) {
        return createClient(endPoints,null,null,null);
    }

    static String getDocumentMetadata(){
        return null;
    }

    static RestClient createClient(String[] endPoints,String username,String password,String keystorePath) {
        try {
            HttpHost[] httpHosts = new HttpHost[endPoints.length];
            int i = 0;
            for(String address:endPoints){
                URL url = new URL(address);
                httpHosts[i] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                i++;
            }
            RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
            if (username != null) {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                restClientBuilder.setHttpClientConfigCallback(
                        httpAsyncClientBuilder ->
                                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }
            if (keystorePath != null && !keystorePath.isEmpty()) {
                KeyStore keyStore = KeyStore.getInstance("jks");
                try (InputStream is = new FileInputStream(new File(keystorePath))) {
                    String keystorePassword = password;
                    keyStore.load(is, (keystorePassword == null) ? null : keystorePassword.toCharArray());
                }
                final SSLContext sslContext =
                        SSLContexts.custom()
                                .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                                .build();
                final SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sslContext);
                restClientBuilder.setHttpClientConfigCallback(
                        httpClientBuilder ->
                                httpClientBuilder.setSSLContext(sslContext).setSSLStrategy(sessionStrategy));
            }

            return restClientBuilder.build();

        }catch (Exception e){
            throw DataXException.asDataXException(ElasticWriterErrorCode.BAD_CONNECT, e);
        }
    }


    static Request getRequest(String method, String endpoint, HttpEntity entity, Map<String, String> params ){
        checkArgument(StringUtils.isNotBlank(method) || StringUtils.isNotBlank(endpoint) ,"request method or endpoint is null");
        Request request = new Request(method,endpoint);
        if(entity != null){
            request.setEntity(entity);
        }
        if(params !=null && !params.isEmpty()){
            try {
                params.forEach((k,v)->{
                    request.addParameter(k,v);
                });
            }catch (Exception e){
                throw DataXException.asDataXException(ElasticWriterErrorCode.BULK_REQ_ERROR, e);
            }

        }
        return request;
    }

    static Request getRequest(String method, String endpoint, Map<String, String> params ){
        return getRequest(method,endpoint,null,params);
    }

    static Request getRequest(String method, String endpoint, HttpEntity entity){
        return getRequest(method,endpoint,entity,null);
    }

    static Request getRequest(String method, String endpoint){
        return getRequest(method,endpoint,null,null);
    }

}
