package org.apache.linkis.engineconnplugin.datax.setting

import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.context.{DataxEngineConnContext, EnvironmentContext}

trait Settings {

  def setEnvironmentContext(engineCreationContext: EngineCreationContext, context: EnvironmentContext): Unit

  def setExecutionContext(engineCreationContext: EngineCreationContext, context: DataxEngineConnContext): Unit

}
