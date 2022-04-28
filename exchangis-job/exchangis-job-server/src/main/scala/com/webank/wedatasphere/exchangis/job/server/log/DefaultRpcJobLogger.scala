package com.webank.wedatasphere.exchangis.job.server.log

import com.webank.wedatasphere.exchangis.job.listener.JobLogListener
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent
import com.webank.wedatasphere.exchangis.job.listener.events.JobLogEvent.Level
import org.slf4j.{Logger, LoggerFactory}

/**
  * Custom job logger, use log4j to record job logs
 */
class DefaultRpcJobLogger extends JobLogListener{

  override def getLogger: Logger = DefaultRpcJobLogger.LOG

  /**
   * Listen the event
   *
   * @param event event
   */
  override def onEvent(event: JobLogEvent): Unit = {
    val message = s"[${event.getTenancy}:${event.getJobExecutionId}] ${event.getMessage}"
    event.getLevel match {
      case Level.INFO => getLogger.info(message, event.getArgs: _*)
      case Level.ERROR => getLogger.error(message, event.getArgs: _*)
      case Level.WARN => getLogger.warn(message, event.getArgs: _*)
      case _ => getLogger.trace(message, event.getArgs)
    }
  }
}

object DefaultRpcJobLogger{
  private final val LOG: Logger = LoggerFactory.getLogger(this.getClass)
}
