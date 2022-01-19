package com.webank.wedatasphere.exchangis.job.server.log

import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.config.plugins.{Plugin, PluginAttribute, PluginElement, PluginFactory}
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.{Filter, Layout, LogEvent}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
  * Custom appender
 */
@Plugin(name = "RpcLog", category = "Core", elementType = "appender", printObject = true)
class RpcJobLogAppender(name: String, filter: Filter,
                        layout: Layout[_ <: Serializable], ignoreExceptions: Boolean, properties: Array[Property]) extends AbstractAppender(name, filter, layout, ignoreExceptions, properties){
  override def append(logEvent: LogEvent): Unit = {
      val content = new String(getLayout.toByteArray(logEvent))
      RpcJobLogAppender.jobLogService match {
        case jobLogService: JobLogService =>
         jobLogService.appendLog("", "", Array[String](content).toList)
        case _ => RpcJobLogAppender.LOG.error(s"Job Log Service cannot find ! lost log: [${content}]")
      }
  }
}

object RpcJobLogAppender{

  final val LOG: Logger = LoggerFactory.getLogger(classOf[RpcJobLogAppender])

  lazy final val jobLogService: JobLogService = SpringContextHolder.getBean(classOf[JobLogService])

  @PluginFactory
  def createAppender(@PluginAttribute("name")name: String, @PluginAttribute("Filter")filter: Filter,
                     @PluginElement("Layout") layout: Layout[_ <: Serializable],
                    @PluginAttribute("ignoreExceptions")ignoreExceptions: Boolean): RpcJobLogAppender = {
      var rpcAppender: RpcJobLogAppender = null
      Option(name).foreach(name => {
          val layoutDef: Layout[_] = Option(layout).getOrElse(PatternLayout.createDefaultLayout())
          rpcAppender = new RpcJobLogAppender(name, filter, layoutDef.asInstanceOf[Layout[_ <: Serializable]], ignoreExceptions, Property.EMPTY_ARRAY)
      })
      rpcAppender
  }
}
