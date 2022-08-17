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

package org.apache.linkis.engineconnplugin.datax.executor

import org.apache.linkis.common.utils.Utils
import org.apache.linkis.engineconn.core.EngineConnObject
import org.apache.linkis.engineconn.once.executor.{ManageableOnceExecutor, OnceExecutorExecutionContext}
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext
import org.apache.linkis.manager.common.entity.resource.{CommonNodeResource, LoadResource, NodeResource}
import org.apache.linkis.manager.engineplugin.common.conf.EngineConnPluginConf

import scala.collection.JavaConversions.mapAsScalaMap

trait DataxOnceExecutor extends ManageableOnceExecutor with DataxExecutor {

  val id: Long

  /**
   * Submit entrance
   * @param onceExecutorExecutionContext execution context
   */
  override protected def submit(onceExecutorExecutionContext: OnceExecutorExecutionContext): Unit = {
    val options = onceExecutorExecutionContext.getOnceExecutorContent.getJobContent.map {
      case (k, v: String) => k -> v
      case (k, v) if v != null => k -> v.toString
      case (k, _) => k -> null
    }.toMap
    doSubmit(onceExecutorExecutionContext, options)
  }

  def doSubmit(onceExecutorExecutionContext: OnceExecutorExecutionContext, options: Map[String, String]): Unit

  override protected val dataxEngineConnContext: DataxEngineConnContext

  override def getCurrentNodeResource(): NodeResource = {
    val properties = EngineConnObject.getEngineCreationContext.getOptions
    val resource = new LoadResource(
      EngineConnPluginConf.JAVA_ENGINE_REQUEST_MEMORY.getValue(properties).toLong,
      EngineConnPluginConf.JAVA_ENGINE_REQUEST_CORES.getValue(properties)
    )
    val engineResource = new CommonNodeResource
    engineResource.setUsedResource(resource)
    engineResource
  }

  override def ensureAvailable[A](f: => A): A = {
    // Not need to throws exception
    Utils.tryQuietly{ super.ensureAvailable(f) }
  }

}











