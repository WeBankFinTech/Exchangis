package com.webank.wedatasphere.exchangis.common.linkis.client

import com.webank.wedatasphere.exchangis.common.linkis.client.config.{ExchangisClientConfig}
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.linkis.httpclient.dws.DWSHttpClient

import java.util.concurrent.TimeUnit

/**
 * Enhanced http client config
 */
class ExchangisHttpClient(clientConfig: ExchangisClientConfig, clientName: String)
  extends DWSHttpClient(clientConfig, clientName){
  /**
   * Build http client
   */
  override protected val httpClient: CloseableHttpClient = {
    val defaultRequestConfig = RequestConfig.custom()
      .setConnectTimeout(clientConfig.getConnectTimeout.toInt)
      .setConnectionRequestTimeout(clientConfig.getConnReqTimeout.toInt)
      .setSocketTimeout(clientConfig.getReadTimeout.toInt)
      .build()
    val clientBuilder = HttpClients.custom()
    clientBuilder.setDefaultRequestConfig(defaultRequestConfig).useSystemProperties()
      .setMaxConnPerRoute(clientConfig.getMaxConnection / 2).setMaxConnTotal(clientConfig.getMaxConnection)
    val maxIdleTime = clientConfig.getMaxIdleTime
    if (maxIdleTime > 0){
      // Evict idle connections
      clientBuilder.evictExpiredConnections();
      clientBuilder.evictIdleConnections(maxIdleTime, TimeUnit.MILLISECONDS)
    }
    clientBuilder.build()
  }

  def getHttpClient: CloseableHttpClient = {
    httpClient
  }
}
