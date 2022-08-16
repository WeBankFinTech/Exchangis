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

package org.apache.linkis.engineconnplugin.datax.launch

import org.apache.linkis.common.utils.JsonUtils
import org.apache.linkis.engineconnplugin.datax.config.DataxConfiguration
import org.apache.linkis.engineconnplugin.datax.plugin.{PluginBmlResource, PluginResource}
import org.apache.linkis.manager.common.protocol.bml.BmlResource
import org.apache.linkis.manager.engineplugin.common.launch.entity.EngineConnBuildRequest
import org.apache.linkis.manager.engineplugin.common.launch.process.JavaProcessEngineConnLaunchBuilder

import java.util

/**
 * Datax engine conn launch builder
 * (use public module lib)
 */
class DataxEngineConnLaunchBuilder extends JavaProcessEngineConnLaunchBuilder {
  protected override def getBmlResources(implicit engineConnBuildRequest: EngineConnBuildRequest): util.List[BmlResource] = {
    val bmlResources = new util.ArrayList[BmlResource](super.getBmlResources)
    val props = engineConnBuildRequest.engineConnCreationDesc.properties
    DataxConfiguration.PLUGIN_RESOURCES.getValue(props) match {
      case resources: String =>
        val mapper = JsonUtils.jackson
        val pluginBmlResources: Array[PluginBmlResource] = mapper.readValue(resources,
          mapper.getTypeFactory.constructArrayType(classOf[PluginBmlResource]))
        Option(pluginBmlResources).foreach(pluginBmlResources => pluginBmlResources.foreach(pluginBmlResource =>{
          // Convert to bml resources
          val bmlResource = new BmlResource
          bmlResource.setFileName(pluginBmlResource.getName)
          bmlResource.setResourceId(pluginBmlResource.getResourceId)
          bmlResource.setVersion(pluginBmlResource.getVersion)
          bmlResource.setOwner(pluginBmlResource.getCreator)
          // Import: must be a public bml resource
          bmlResource.setVisibility(BmlResource.BmlResourceVisibility.Public)
          bmlResources.add(bmlResource)
        }))
    }
    bmlResources
  }

}
