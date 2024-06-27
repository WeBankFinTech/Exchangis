package org.apache.linkis.engineconnplugin.datax.service

import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.engineconn.acessible.executor.info.NodeHeartbeatMsgManager
import org.apache.linkis.engineconn.executor.entity.Executor
import org.apache.linkis.engineconnplugin.datax.executor.DataxContainerOnceExecutor
import org.apache.linkis.server.BDPJettyServerHelper

import scala.collection.JavaConverters.mapAsScalaMapConverter

/**
 * Datax heartbeat message (include: metric, error message)
 */
class DataxHeartbeatMsgManager extends NodeHeartbeatMsgManager with Logging{
  override def getHeartBeatMsg(executor: Executor): String = {
    executor match {
      case dataxExecutor: DataxContainerOnceExecutor =>
        val metric = dataxExecutor.getMetrics
        Utils.tryCatch(BDPJettyServerHelper.gson.toJson(metric)) { case e: Exception =>
          val mV = metric.asScala
            .map { case (k, v) => if (null == v) s"${k}->null" else s"${k}->${v.toString}" }
            .mkString(",")
          val errMsg = e.getMessage
          logger.error(s"Convert metric to json failed because : ${errMsg}, metric values : {${mV}}")
          "{\"errorMsg\":\"Convert metric to json failed because : " + errMsg + "\"}"
        }
      case _ => "{}"
    }
  }
}
