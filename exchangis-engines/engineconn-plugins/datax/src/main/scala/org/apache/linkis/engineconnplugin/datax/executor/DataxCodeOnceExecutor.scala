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
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconn.once.executor.{OnceExecutorExecutionContext, OperableOnceExecutor}
import org.apache.linkis.engineconnplugin.datax.config.exception.JobExecutionException
import org.apache.linkis.engineconnplugin.datax.config.{DataxEngine, DataxEnvConfiguration}
import org.apache.linkis.engineconnplugin.datax.context.DataxEngineConnContext
import org.apache.linkis.engineconnplugin.datax.params.DataxParamsResolver
import org.apache.linkis.manager.common.entity.enumeration.NodeStatus
import org.apache.linkis.protocol.engine.JobProgressInfo
import org.apache.linkis.scheduler.executer.ErrorExecuteResponse

import java.util
import java.util.concurrent.{Future, TimeUnit}

class DataxCodeOnceExecutor(override val id: Long,
                            override protected val dataxEngineConnContext: DataxEngineConnContext) extends DataxOnceExecutor with OperableOnceExecutor {

  private var params: util.Map[String, String] = _
  private var future: Future[_] = _
  private var daemonThread: Future[_] = _
  private val paramsResolvers: Array[DataxParamsResolver] = Array()

  override def doSubmit(onceExecutorExecutionContext: OnceExecutorExecutionContext, options: Map[String, String]): Unit = {
    var isFailed = false
    future = Utils.defaultScheduler.submit(new Runnable {
      override def run(): Unit = {
        // TODO filter job content
        params = onceExecutorExecutionContext.getOnceExecutorContent.getJobContent.asInstanceOf[util.Map[String, String]]
        info("Try to execute params." + params)
        if(runDatax(params, onceExecutorExecutionContext.getEngineCreationContext) != 0) {
          isFailed = true
          tryFailed()
          setResponse(ErrorExecuteResponse("Run code failed!", new JobExecutionException("Exec Datax Code Error")))
        }
        info("All codes completed, now to stop SqoopEngineConn.")
//        closeDaemon()
        if (!isFailed) {
          trySucceed()
        }
        this synchronized notify()
      }
    })
  }

  protected def runDatax(params: util.Map[String, String], context: EngineCreationContext): Int = {
    Utils.tryCatch {
      val finalParams = paramsResolvers.foldLeft(params) {
        case (newParam, resolver) => resolver.resolve(newParam, context)
      }
      DataxEngine.run(finalParams)
    }{
      case e: Exception =>
        error(s"Run Error Message: ${e.getMessage}", e)
        -1
    }
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

  override def getProgress: Float = {
    DataxEngine.progress()
  }

  override def getProgressInfo: Array[JobProgressInfo] = {
    Array(DataxEngine.getProgressInfo)
  }

  override def getMetrics: util.Map[String, Any] = {
    DataxEngine.getMetrics.asInstanceOf[util.Map[String, Any]]
  }

  override def getDiagnosis: util.Map[String, Any] = {
    DataxEngine.getDiagnosis.asInstanceOf[util.Map[String, Any]]
  }

  override def isClosed: Boolean = {
    NodeStatus.isCompleted(getStatus)
  }

}
