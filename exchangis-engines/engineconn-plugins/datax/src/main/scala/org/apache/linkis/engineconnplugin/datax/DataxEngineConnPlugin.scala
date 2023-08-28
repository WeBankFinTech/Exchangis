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

package org.apache.linkis.engineconnplugin.datax

import org.apache.linkis.engineconnplugin.datax.factory.DataxEngineConnFactory
import org.apache.linkis.engineconnplugin.datax.launch.DataxEngineConnLaunchBuilder
import org.apache.linkis.engineconnplugin.datax.resource.DataxEngineConnResourceFactory
import org.apache.linkis.manager.engineplugin.common.EngineConnPlugin
import org.apache.linkis.manager.engineplugin.common.creation.EngineConnFactory
import org.apache.linkis.manager.engineplugin.common.launch.EngineConnLaunchBuilder
import org.apache.linkis.manager.engineplugin.common.resource.EngineResourceFactory
import org.apache.linkis.manager.label.entity.Label

import java.util.List
import java.util.ArrayList

class DataxEngineConnPlugin extends EngineConnPlugin {

  private var engineResourceFactory: EngineResourceFactory = _
  private val engineResourceFactoryLocker = new Array[Byte](0)

  private var engineConnFactory: EngineConnFactory = _
  private val engineConnFactoryLocker = new Array[Byte](0)

  override def init(params: java.util.Map[_root_.scala.Predef.String, scala.AnyRef]): Unit = {}

  override def getEngineResourceFactory: EngineResourceFactory = {
    if (null == engineResourceFactory) engineResourceFactoryLocker.synchronized {
      if (null == engineResourceFactory) {
        engineResourceFactory = new DataxEngineConnResourceFactory
      }
    }
    engineResourceFactory
  }

  override def getEngineConnLaunchBuilder: EngineConnLaunchBuilder = {
    new DataxEngineConnLaunchBuilder
  }

  override def getEngineConnFactory: EngineConnFactory = {
    if (null == engineConnFactory) engineConnFactoryLocker.synchronized {
      if (null == engineConnFactory) {
        engineConnFactory = new DataxEngineConnFactory
      }
    }
    engineConnFactory
  }

  override def getDefaultLabels: List[Label[_]] = {
    new ArrayList[Label[_]]()
  }
}
