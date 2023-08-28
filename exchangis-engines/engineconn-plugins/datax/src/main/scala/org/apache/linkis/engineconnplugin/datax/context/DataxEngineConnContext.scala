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

package org.apache.linkis.engineconnplugin.datax.context

import com.alibaba.datax.common.util.Configuration
import java.util
/**
 * Datax engine conn context
 */
class DataxEngineConnContext(settings: Configuration, coreConfig: Configuration, pluginDefinitions: util.List[DataxPluginDefinition]) {

  /**
   * Plugin definition
   * @return
   */
  def getPluginDefinitions: util.List[DataxPluginDefinition] = pluginDefinitions

  /**
   * Settings configuration
   * @return
   */
  def getSettings: Configuration = settings

  /**
   * Core configuration
   * @return
   */
  def getCoreConfig: Configuration = coreConfig
}
