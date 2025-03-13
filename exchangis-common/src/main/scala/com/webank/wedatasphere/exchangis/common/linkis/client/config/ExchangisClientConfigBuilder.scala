package com.webank.wedatasphere.exchangis.common.linkis.client.config

import com.webank.wedatasphere.exchangis.common.http.HttpClientConfiguration
import com.webank.wedatasphere.exchangis.common.linkis.client.ClientConfiguration
import org.apache.linkis.httpclient.config.{ClientConfig, ClientConfigBuilder}
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy

import java.util.concurrent.TimeUnit

/**
 * Enhanced dws client config builder
 */
class ExchangisClientConfigBuilder extends ClientConfigBuilder{

  private var maxIdleTime: Long = _

  private var connReqTimeout: Long = _

  private var dwsVersion: String = _

  // Load from vars default
  // Http common
  maxConnection = HttpClientConfiguration.MAX_CONNECTION_SIZE.getValue
  connectTimeout = HttpClientConfiguration.CONNECTION_TIMEOUT.getValue.longValue()
  readTimeout = HttpClientConfiguration.SOCKET_READ_TIMEOUT.getValue.longValue()
  // Linkis client, use token auth default
  dwsVersion = ClientConfiguration.LINKIS_DWS_VERSION.getValue
  serverUrl = ClientConfiguration.LINKIS_SERVER_URL.getValue
  discoveryEnabled = ClientConfiguration.LINKIS_DISCOVERY_ENABLED.getValue
  discoveryFrequency(ClientConfiguration.LINKIS_DISCOVERY_FREQUENCY_PERIOD.getValue, TimeUnit.MINUTES)
  loadbalancerEnabled = ClientConfiguration.LINKIS_LOAD_BALANCER_ENABLED.getValue
  retryEnabled = ClientConfiguration.LINKIS_RETRY_ENABLED.getValue
  authenticationStrategy = new TokenAuthenticationStrategy()
  authTokenKey = TokenAuthenticationStrategy.TOKEN_KEY
  authTokenValue = ClientConfiguration.LINKIS_TOKEN_VALUE.getValue

  def maxIdleTime(maxIdleTime: Long): this.type = {
    this.maxIdleTime = maxIdleTime
    this
  }

  def connReqTimeout(connReqTimeout: Long): this.type = {
    this.connReqTimeout = connReqTimeout
    this
  }

  def setDWSVersion(dwsVersion: String): this.type = {
    this.dwsVersion = dwsVersion
    this
  }

  override def build(): ExchangisClientConfig = {
    val clientConfig = new ExchangisClientConfig(super.build(), maxIdleTime, connReqTimeout)
    clientConfig.setDWSVersion(dwsVersion)
    clientConfig
  }


}
