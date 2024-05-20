package com.webank.wedatasphere.exchangis.common.linkis.client.config

import org.apache.linkis.httpclient.config.ClientConfig
import org.apache.linkis.httpclient.dws.config.DWSClientConfig

/**
 * Enhanced dws client config
 */
class ExchangisClientConfig private[config](
    clientConfig: ClientConfig,
    maxIdleTime: Long,
    connReqTimeout: Long
 ) extends DWSClientConfig(clientConfig) {

    /**
     * Max idle time
     * @return
     */
    def getMaxIdleTime: Long = {
        maxIdleTime
    }

  /**
   * Connection request timeout
   * @return
   */
  def getConnReqTimeout: Long = {
        connReqTimeout
    }
}

object ExchangisClientConfig{
  def newBuilder: ExchangisClientConfigBuilder = {
    new ExchangisClientConfigBuilder()
  }
}