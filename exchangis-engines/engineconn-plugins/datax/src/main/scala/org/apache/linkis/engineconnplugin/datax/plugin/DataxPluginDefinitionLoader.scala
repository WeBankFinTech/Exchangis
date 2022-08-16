package org.apache.linkis.engineconnplugin.datax.plugin

import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.context.DataxPluginDefinition
import java.util
/**
 * Plugin definition loader
 */
trait DataxPluginDefinitionLoader {

  /**
   * Load plugin
   * @param engineCreationContext engine create context
   * @return
   */
  def loadPlugin(engineCreationContext: EngineCreationContext): util.List[DataxPluginDefinition]
}
