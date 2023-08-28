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

import com.alibaba.datax.common.element.ColumnCast
import com.alibaba.datax.common.exception.DataXException
import com.alibaba.datax.common.statistics.{PerfTrace, VMInfo}
import com.alibaba.datax.common.util.Configuration
import com.alibaba.datax.core.AbstractContainer
import com.alibaba.datax.core.util.container.{CoreConstant, LoadUtil}
import com.alibaba.datax.core.util.{ConfigurationValidate, ExceptionTracker, FrameworkErrorCode, SecretUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.{ClassUtils, Utils}
import org.apache.linkis.engineconn.common.creation.EngineCreationContext
import org.apache.linkis.engineconn.once.executor.{OnceExecutorExecutionContext, OperableOnceExecutor}
import org.apache.linkis.engineconnplugin.datax.config.DataxConfiguration
import org.apache.linkis.engineconnplugin.datax.exception.{DataxJobExecutionException, DataxPluginLoadException}
import org.apache.linkis.engineconnplugin.datax.executor.DataxContainerOnceExecutor.{CODE_NAME, JOB_CONTENT_NAME}
import org.apache.linkis.engineconnplugin.datax.report.{BasicDataxReportReceiver, DataxReportReceiver}
import org.apache.linkis.engineconnplugin.datax.utils.SecretUtils
import org.apache.linkis.manager.common.entity.enumeration.NodeStatus
import org.apache.linkis.protocol.engine.JobProgressInfo
import org.apache.linkis.scheduler.executer.ErrorExecuteResponse

import java.util
import java.util.concurrent.{Future, TimeUnit}
import scala.collection.JavaConverters._

/**
 * Once executor for datax container
 */
abstract class DataxContainerOnceExecutor extends DataxOnceExecutor with OperableOnceExecutor {
  /**
   * Executor configuration
   */
  private var execConfiguration: Configuration = _
  /**
   * Future
   */
  private var future: Future[_] = _
  private var daemonThread: Future[_] = _

  /**
   * Report receiver
   */
  private var reportReceiver: DataxReportReceiver = _

  /**
   * Container
   */
  private var container: AbstractContainer = _
  override def getId: String = "DataxOnceApp_" + getContainerName + "_" + id

  override def doSubmit(onceExecutorExecutionContext: OnceExecutorExecutionContext, options: Map[String, String]): Unit = {
    if (StringUtils.isNotBlank(DataxConfiguration.SECURITY_MANAGER_CLASSES.getValue)) {
      // Set the security manager
      System.setSecurityManager(ClassUtils.getClassInstance(DataxConfiguration.SECURITY_MANAGER_CLASSES.getValue))
    }
    // Init the report receiver
    if (Option(reportReceiver).isEmpty) reportReceiver = new BasicDataxReportReceiver()
    var isFailed = false
    future = Utils.defaultScheduler.submit(new Runnable {
      override def run(): Unit = {
        val params: util.Map[String, Object] = onceExecutorExecutionContext.getOnceExecutorContent.getJobContent
        val result = execute(params, onceExecutorExecutionContext.getEngineCreationContext)
        if (result._1 != 0) {
          isFailed = true
          tryFailed()
          val message = s"Exec Datax engine conn occurred error, with exit code: [${result._1}]"
          setResponse(ErrorExecuteResponse(message, new DataxJobExecutionException(message, result._2)))
        }
        info(s"The executor: [${getId}]  has been finished, now to stop DataxEngineConn.")
        closeDaemon()
        if (!isFailed) {
          trySucceed()
        }
        this synchronized notify()
      }
    })
  }

  /**
   * Wait to running
   */
  override protected def waitToRunning(): Unit = {
    if (!isClosed) daemonThread = Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        if (!(future.isDone || future.isCancelled)) {
          trace(s"The executor: [$getId] has been still running")
        }
      }
    }, DataxConfiguration.STATUS_FETCH_INTERVAL.getValue.toLong,
      DataxConfiguration.STATUS_FETCH_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)
  }

  /**
   * Get report receiver
   * @return
   */
  def getReportReceiver: DataxReportReceiver = this.reportReceiver

  /**
   * Get container
   * @return
   */
  def getContainer: AbstractContainer = this.container
  override def getProgress: Float = {
    Option(this.reportReceiver) match {
      case Some(_) => this.reportReceiver.getProgress
      case _ => 0f
    }
  }

  override def getProgressInfo: Array[JobProgressInfo] = {
    Option(this.reportReceiver) match {
      case Some(_) => this.reportReceiver.getProgressInfo
      case _ => Array()
    }
  }

  override def getMetrics: util.Map[String, Any] = {
    val metrics = Option(this.reportReceiver) match {
      case Some(_) => this.reportReceiver.getMetrics
      case _ => new util.HashMap[String, Any]()
    }
    // Report the resource
    metrics.put("NodeResourceJson", getCurrentNodeResource().getUsedResource.toJson)
    metrics
  }

  override def getDiagnosis: util.Map[String, Any] = {
    // Not support diagnosis
    new util.HashMap[String, Any]()
  }

  override def isClosed: Boolean = {
    NodeStatus.isCompleted(getStatus)
  }

  override def tryFailed(): Boolean = {
//    Option(this.container).foreach(_.shutdown())
    super.tryFailed()
  }
  /**
   * Execute with job content
   * @param jobContent job content
   * @param engineCreateContext engine create context
   * @return
   */
  private def execute(jobContent: util.Map[String, Object], engineCreateContext: EngineCreationContext):(Int, Throwable) = {
    var exitCode: Int = 0
    var throwable: Throwable = null
    Utils.tryCatch {
      trace("Begin to decrypt the job content")
      var fullConfig: Configuration = Configuration.from(jobContent)
      fullConfig = SecretUtil.decryptSecretKey(fullConfig)
      // Add the settings to job content
      mergeConfig(fullConfig, dataxEngineConnContext.getSettings, CODE_NAME, updateWhenConflict = false)
      // Add the core configuration to job content
      mergeConfig(fullConfig, dataxEngineConnContext.getCoreConfig, "", updateWhenConflict = true)
      // Print VM information
      // Set plugin configuration
      setPluginConfig(fullConfig)
      Option(VMInfo.getVmInfo) match {
        case Some(vm) => info(vm.toString)
        case _ =>
      }
      info(s"Try to launch executor: [${getId}] with job content: \n ${maskJobContent(fullConfig)}.\n")
      // Seems that it is not important?
      ConfigurationValidate.doValidate(fullConfig)
      // Init environment settings
      initEnvWithConfig(fullConfig)
      // Store the full configuration
      this.execConfiguration = fullConfig
      execute(this.execConfiguration, engineCreateContext)
    } {
      e: Throwable =>
        exitCode = 1
        throwable = e
        error(s"The possible reason of problem is : \n ${ExceptionTracker.trace(e)}")
        e match {
          case dataxE: DataXException =>
            val errorCode = dataxE.getErrorCode
            errorCode match {
              case code: FrameworkErrorCode =>
                exitCode = code.toExitValue
              case _ =>
            }
          case _ =>
        }
    }
    (exitCode, throwable)
  }

  /**
   * Execute with configuration
   * @param self configuration
   * @param engineCreateContext engine create context
   */
  private def execute(self: Configuration, engineCreateContext: EngineCreationContext): Unit = {
     // PrefTrace
     val traceEnable = self.getBool(CoreConstant.DATAX_CORE_CONTAINER_TRACE_ENABLE, true)
     val perfReportEnable = self.getBool(CoreConstant.DATAX_CORE_REPORT_DATAX_PERFLOG, true)
     val jobInfo = self.getConfiguration(CoreConstant.DATAX_JOB_JOBINFO)
     val channelNumber = self.getInt(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_CHANNEL)
     val isJob = this.isInstanceOf[DataxJobOnceExecutor]
     val taskGroupId: Int = if (isJob) -1 else self.getInt(CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_ID)
     val perfTrace = PerfTrace.getInstance(isJob, self.getLong(CoreConstant.DATAX_CORE_CONTAINER_JOB_ID), taskGroupId, 0, traceEnable)
     perfTrace.setJobInfo(jobInfo, perfReportEnable, channelNumber)
     Option(createContainer(self, engineCreateContext)).foreach(container => {
       this.container = container
       container.start()
     })
  }
  /**
   * Set plugin configuration
   * @param self self configuration
   */
  private def setPluginConfig(self: Configuration): Unit = {
    val plugins: util.Map[String, Configuration] = dataxEngineConnContext
      .getPluginDefinitions.asScala.map(define => (define.getPluginName, define.getPluginConf)).toMap.asJava
    val pluginsNeed: util.Map[String, Configuration] = new util.HashMap()
    Option(self.getString(CoreConstant.DATAX_JOB_CONTENT_READER_NAME)).foreach(readerPlugin => pluginsNeed.put(readerPlugin, plugins.get(readerPlugin)))
    Option(self.getString(CoreConstant.DATAX_JOB_CONTENT_WRITER_NAME)).foreach(writerPlugin => pluginsNeed.put(writerPlugin, plugins.get(writerPlugin)))
    Option(self.getString(CoreConstant.DATAX_JOB_PREHANDLER_PLUGINNAME)).foreach(prePlugin => pluginsNeed.put(prePlugin, plugins.get(prePlugin)))
    Option(self.getString(CoreConstant.DATAX_JOB_POSTHANDLER_PLUGINNAME)).foreach(postPlugin => pluginsNeed.put(postPlugin, plugins.get(postPlugin)))
    val noLoadPlugin = pluginsNeed.asScala.filter(entry => entry._2 == null).toMap
    if (noLoadPlugin.nonEmpty){
      throw new DataxPluginLoadException(s"The specific plugins have not been loaded: [${noLoadPlugin.keys.mkString(",")}]", null)
    }
    pluginsNeed.asScala.foreach(entry => {
      val pluginName = entry._1
      if (pluginName.endsWith("reader")){
        self.set(s"plugin.reader.${pluginName}", entry._2)
      } else if (pluginName.endsWith("writer")){
        self.set(s"plugin.writer.${pluginName}", entry._2)
      } else {
        throw new DataxPluginLoadException(s"Unrecognized plugin name: [${pluginName}], please redefine it", null)
      }
    })
  }
  /**
   * Merge configuration
   * @param self self configuration
   * @param another another configuration
   * @param pathPrefix path prefix
   * @param updateWhenConflict update when conflict
   * @return
   */
  private def mergeConfig(self: Configuration, another: Configuration, pathPrefix: String,
                          updateWhenConflict: Boolean): Unit = {
    val keys = another.getKeys
    keys.asScala.foreach(key => {
      val combineKey: String = if (StringUtils.isNotBlank(pathPrefix))
        StringUtils.join(util.Arrays.asList(pathPrefix, key), ".") else key
      if (updateWhenConflict){
        self.set(combineKey, another.get(key))
      } else {
        Option(self.get(combineKey)) match {
          case Some(_) =>
          case _ => self.set(combineKey, another.get(key))
        }
      }
    })
  }

  /**
   * Init the environment with configuration
   * @param self self
   */
  private def initEnvWithConfig(self: Configuration): Unit = {
     ColumnCast.bind(self)
     LoadUtil.bind(self)
  }
  /**
   * Mask the job content
   * @param self self configuration
   * @return
   */
  private def maskJobContent(self: Configuration): String = {
    val contentWithSettings = self.getConfiguration(CODE_NAME).clone()
    val content: Configuration = contentWithSettings.getConfiguration(JOB_CONTENT_NAME)
    SecretUtils.filterSensitiveConfiguration(content)
    contentWithSettings.set(JOB_CONTENT_NAME, content)
    contentWithSettings.beautify()
  }

  protected def closeDaemon(): Unit = {
    if (daemonThread != null) daemonThread.cancel(true)
  }
  /**
   * Container name
   * @return
   */
  def getContainerName: String


  /**
   * Create container
   * @param config container configuration
   * @param engineCreateContext engine create context
   */
  def createContainer(config: Configuration, engineCreateContext: EngineCreationContext): AbstractContainer
}

object DataxContainerOnceExecutor{

  val CODE_NAME: String = "job"

  val JOB_CONTENT_NAME = "content"


}
