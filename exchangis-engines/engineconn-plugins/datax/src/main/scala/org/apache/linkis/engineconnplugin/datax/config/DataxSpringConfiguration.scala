package org.apache.linkis.engineconnplugin.datax.config

import org.apache.linkis.common.utils.Logging
import org.apache.linkis.engineconn.acessible.executor.info.NodeHeartbeatMsgManager
import org.apache.linkis.engineconnplugin.datax.service.DataxHeartbeatMsgManager
import org.springframework.context.annotation.{Bean, Configuration, Primary}

/**
 * Spring configuration for datax
 */
@Configuration
class DataxSpringConfiguration extends Logging {

  /**
   * Override the heartbeat manager
   * @return
   */
  @Bean
  @Primary
  def nodeHeartbeatMsgManager(): NodeHeartbeatMsgManager = {
    new DataxHeartbeatMsgManager()
  }
}
