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

import com.alibaba.datax.core.util.container.CoreConstant
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconn.common.engineconn.EngineConn
import org.apache.linkis.engineconn.once.executor.OnceExecutor
import org.apache.linkis.engineconn.once.executor.creation.OnceExecutorFactory
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext
import org.apache.linkis.engineconnplugin.datax.executor.{DataxContainerOnceExecutor, DataxJobOnceExecutor, DataxTaskGroupOnceExecutor}
import org.apache.linkis.manager.label.entity.Label
import org.apache.linkis.manager.label.entity.engine.RunType
import org.apache.linkis.manager.label.entity.engine.RunType.{JAVA, RunType, SCALA}

class DataxCodeExecutorFactory extends OnceExecutorFactory {
  protected override def newExecutor(id: Int,
                                     engineCreationContext: EngineCreationContext,
                                     engineConn: EngineConn,
                                     labels: Array[Label[_]]): OnceExecutor = {
    engineConn.getEngineConnSession match {
      case context: DataxEngineConnContext =>
        val isJob = !("taskGroup".equalsIgnoreCase(context.getCoreConfig
        .getString(CoreConstant.DATAX_CORE_CONTAINER_MODEL)))
        if (isJob)
          new DataxJobOnceExecutor(id, context)
        else new DataxTaskGroupOnceExecutor(id, context)
      case _ => null
    }
  }

  override protected def getSupportRunTypes: Array[String] = Array(SCALA.toString, JAVA.toString)

  override protected def getRunType: RunType = RunType.SCALA
}
