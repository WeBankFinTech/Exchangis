/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.linkis.engineconnplugin.datax.factory

import com.alibaba.datax.common.util.Configuration
import com.alibaba.datax.core.util.container.CoreConstant
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.common.utils.Logging
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconnplugin.datax.config.DataxConfiguration
import org.apache.linkis.engineconnplugin.datax.config.DataxConfiguration.CONFIG_PREFIX
import org.apache.linkis.engineconnplugin.datax.config.DataxCoreConfiguration._
import org.apache.linkis.engineconnplugin.datax.config.DataxSettingConfiguration._
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext
import org.apache.linkis.engineconnplugin.datax.factory.DataxEngineConnFactory.{CORE_ARRAY_CONFIGS, CORE_VALUE_CONFIGS, SETTING_VALUE_CONFIGS}
import org.apache.linkis.engineconnplugin.datax.plugin.{DataxPluginDefinitionLoader, LocalDataxPluginDefinitionLoader}
import org.apache.linkis.manager.engineplugin.common.creation.{ExecutorFactory, MultiExecutorEngineConnFactory}
import org.apache.linkis.manager.label.entity.engine.EngineType
import org.apache.linkis.manager.label.entity.engine.EngineType.EngineType

import java.util
import scala.collection.JavaConverters._

/**
 * Datax engine conn factory
 */
class DataxEngineConnFactory extends MultiExecutorEngineConnFactory with Logging {

  /**
   * Plugin loader
   */
  private val pluginLoader: DataxPluginDefinitionLoader = LocalDataxPluginDefinitionLoader()

  override protected def getEngineConnType: EngineType = EngineType.DATAX

  override protected def createEngineConnSession(engineCreationContext: EngineCreationContext): Any = {
    var options = engineCreationContext.getOptions
    options = options.asScala.map{
      case (key, value) =>
        if (key.startsWith(CONFIG_PREFIX)){
          (key.replaceFirst(CONFIG_PREFIX, ""), value)
        } else (key, value)
    }.asJava
    engineCreationContext.setOptions(options)
    val coreConfig = createCoreConfiguration(engineCreationContext)
    val settings = createSettingsConfiguration(engineCreationContext)
    new DataxEngineConnContext(settings, coreConfig, pluginLoader.loadPlugin(engineCreationContext))
  }

  /**
   * Core configuration
   * @param engineCreationContext engine create context
   * @return
   */
  private def createCoreConfiguration(engineCreationContext: EngineCreationContext): Configuration = {
    val configuration = Configuration.from("{}")
    val options = engineCreationContext.getOptions
    CORE_VALUE_CONFIGS.foreach(config => config.getValue(options) match {
      case v: Any => configuration.set(config.key, v)
      case _ => //Ignore the unexpected value
    })
    CORE_ARRAY_CONFIGS.foreach(config => config.getValue(options) match {
      case array: Array[String] => configuration.set(config.key, array)
      case str: String => if (StringUtils.isNotBlank(str))
        configuration.set(config.key, util.Arrays.asList(str.split(",")))
      case _ => //Ignore the unrecognized value
    })
    Option(DataxConfiguration.JOB_EXECUTION_ID.getValue(options)) match {
      case Some(executionId: String) =>
        configuration.set(CoreConstant.DATAX_CORE_CONTAINER_JOB_ID, executionId)
      case _ =>
    }
    configuration
  }

  /**
   * Settings configuration
   * @param engineCreationContext engine create context
   * @return
   */
  private def createSettingsConfiguration(engineCreationContext: EngineCreationContext): Configuration = {
    val configuration = Configuration.from("{}")
    SETTING_VALUE_CONFIGS.foreach(config => config.getValue(engineCreationContext.getOptions) match {
      case v: Any => configuration.set(config.key, v)
      case _ => //Ignore the unexpected value
    })
    configuration
  }
  override protected def getDefaultExecutorFactoryClass: Class[_ <: ExecutorFactory] = {
    classOf[DataxCodeExecutorFactory]
  }

  override def getExecutorFactories: Array[ExecutorFactory] = {
    val executorFactoryArray = Array[ExecutorFactory](new DataxCodeExecutorFactory)
    executorFactoryArray
  }

}

object DataxEngineConnFactory{
  /**
   * Settings
   */
  val SETTING_VALUE_CONFIGS: Array[CommonVars[_]] = Array(SETTING_SYNC_META, SETTING_TRANSPORT_TYPE,
    SETTING_KEY_VERSION, SETTING_SPEED_BYTE, SETTING_SPEED_RECORD,
    SETTING_SPEED_CHANNEL, SETTING_ERROR_LIMIT_RECORD, SETTING_USE_PROCESSOR
  )

  /**
   * Core
   */
  val CORE_VALUE_CONFIGS: Array[CommonVars[_]] =
    Array(CORE_STATISTICS_REPORTER_PLUGIN_CLASS, COMMON_COLUMN_DATETIME_FORMAT, COMMON_COLUMN_TIME_FORMAT, COMMON_COLUMN_DATE_FORMAT,
      COMMON_COLUMN_TIMEZONE, COMMON_COLUMN_ENCODING, CORE_TRANSPORT_TYPE, CORE_TRANSPORT_CHANNEL_SPEED_BYTE,
      CORE_TRANSPORT_CHANNEL_SPEED_RECORD, CORE_TRANSPORT_CHANNEL_FLOW_CONTROL_INTERNAL, CORE_TRANSPORT_CHANNEL_CAPACITY,
      CORE_TRANSPORT_CHANNEL_BYTE_CAPACITY, CORE_TRANSPORT_RECORD_CHANNEL_CLASS, CORE_TRANSPORT_RECORD_EXCHANGER_CLASS,
      CORE_TRANSPORT_RECORD_EXCHANGER_BUFFER_SIZE, CORE_TRANSPORT_STREAM_CHANNEL_CLASS, CORE_TRANSPORT_STREAM_CHANNEL_BLOCK_SIZE,
      CORE_CONTAINER_JOB_REPORT_INTERVAL, CORE_CONTAINER_JOB_SLEEP_INTERNAL, CORE_CONTAINER_TASK_GROUP_REPORT_INTERVAL,
      CORE_CONTAINER_TASK_GROUP_SLEEP_INTERNAL, CORE_CONTAINER_TASK_GROUP_CHANNEL, CORE_CONTAINER_TRACE_ENABLE,
      CORE_STATISTICS_COLLECTOR_PLUGIN_TASK_CLASS, CORE_STATISTICS_COLLECTOR_PLUGIN_MAX_DIRTY_NUMBER,
      CORE_PROCESSOR_LOADER_PLUGIN_CLASS, CORE_PROCESSOR_LOADER_PLUGIN_PACKAGE, CORE_PROCESSOR_LOADER_PLUGIN_SOURCE_PATH, CORE_CONTAINER_MODEL
    )

  val CORE_ARRAY_CONFIGS: Array[CommonVars[_]] = Array(COMMON_COLUMN_EXTRA_FORMATS)
}