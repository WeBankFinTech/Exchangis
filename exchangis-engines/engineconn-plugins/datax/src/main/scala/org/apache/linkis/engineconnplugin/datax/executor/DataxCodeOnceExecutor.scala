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
import org.apache.linkis.engineconn.once.executor.{OnceExecutorExecutionContext, OperableOnceExecutor}
import org.apache.linkis.engineconnplugin.datax.config.DataxEnvConfiguration
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext
import org.apache.linkis.manager.common.entity.enumeration.NodeStatus
import org.apache.linkis.protocol.engine.JobProgressInfo

import java.util
import java.util.concurrent.{Future, TimeUnit}

class DataxCodeOnceExecutor(override val id: Long,
                            override protected val dataxEngineConnContext: DataxEngineConnContext) extends DataxOnceExecutor with OperableOnceExecutor {

  private var params: util.Map[String, String] = _
  private var future: Future[_] = _
  private var daemonThread: Future[_] = _

  //todo 实现
  override def doSubmit(onceExecutorExecutionContext: OnceExecutorExecutionContext, options: Map[String, String]): Unit = {

  }

  override protected def waitToRunning(): Unit = {
    if (!isClosed) daemonThread = Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        if (!(future.isDone || future.isCancelled)) {
          info("The Sqoop Process In Running")
        }
      }
    },
      DataxEnvConfiguration.DATAX_STATUS_FETCH_INTERVAL.getValue.toLong,
      DataxEnvConfiguration.DATAX_STATUS_FETCH_INTERVAL.getValue.toLong,
      TimeUnit.MILLISECONDS)
  }

  //todo 下面四个是OperableOnceExecutor的
  override def getProgress: Float = {
    //todo client
  }

  override def getProgressInfo: Array[JobProgressInfo] = {
    //todo client
  }

  override def getMetrics: util.Map[String, Any] = {
    //todo client
  }

  override def getDiagnosis: util.Map[String, Any] = {
    //todo client
  }

  override def isClosed: Boolean = {
    isClosed || NodeStatus.isCompleted(getStatus)
  }

}
