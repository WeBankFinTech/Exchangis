package com.webank.wedatasphere.exchangis.common.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.TimeUnit;

public class HttpGenericClientBuilder {

    private int connTimeout = HttpClientConfiguration.CONNECTION_TIMEOUT.getValue();

    private int connReqTimeout = HttpClientConfiguration.CONNECTION_REQUEST_TIMEOUT.getValue();

    private int socketReadTimeout = HttpClientConfiguration.SOCKET_READ_TIMEOUT.getValue();

    private int maxIdleTime = HttpClientConfiguration.MAX_IDLE_TIME.getValue();

    private int maxConnSize = HttpClientConfiguration.MAX_CONNECTION_SIZE.getValue();

    public static HttpGenericClientBuilder newBuilder() {
        return new HttpGenericClientBuilder();
    }

    public HttpGenericClientBuilder setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
        return this;
    }

    public HttpGenericClientBuilder setConnReqTimeout(int connReqTimeout) {
        this.connReqTimeout = connReqTimeout;
        return this;
    }

    public HttpGenericClientBuilder setSocketReadTimeout(int socketReadTimeout) {
        this.socketReadTimeout = socketReadTimeout;
        return this;
    }

    public HttpGenericClientBuilder setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public HttpGenericClientBuilder setMaxConnSize(int maxConnSize) {
        this.maxConnSize = maxConnSize;
        return this;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public int getConnReqTimeout() {
        return connReqTimeout;
    }

    public int getSocketReadTimeout() {
        return socketReadTimeout;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public int getMaxConnSize() {
        return maxConnSize;
    }

    public HttpClient build() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(connReqTimeout)
                .setSocketTimeout(socketReadTimeout)
                .build();
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setDefaultRequestConfig(requestConfig)
                .useSystemProperties()
                .setMaxConnPerRoute(maxConnSize)
                .setMaxConnTotal(maxConnSize);
        if (this.maxIdleTime > 0) {
            clientBuilder.evictExpiredConnections();
            clientBuilder.evictIdleConnections(this.maxIdleTime, TimeUnit.MILLISECONDS);
        }
        return clientBuilder.build();
    }

}
