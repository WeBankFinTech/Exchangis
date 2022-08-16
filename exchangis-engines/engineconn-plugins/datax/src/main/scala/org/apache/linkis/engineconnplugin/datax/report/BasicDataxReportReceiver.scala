package org.apache.linkis.engineconnplugin.datax.report
import com.alibaba.datax.core.statistics.communication.{Communication, CommunicationTool}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.protocol.engine.JobProgressInfo

import java.util

/**
 * Basic datax report receiver
 */
class BasicDataxReportReceiver extends DataxReportReceiver {

  private var jobId: String =_
  /**
   * Just store the last communication
   */
  private var lastCommunication: Communication = _
  /**
   * Receive communication
   *
   * @param communication communication
   */
  override def receive(jobId: String, communication: Communication): Unit = {
    if (StringUtils.isNotBlank(jobId)){
      this.jobId = jobId
    }
    // Update
    this.lastCommunication = communication
  }

  /**
   * Progress value
   *
   * @return
   */
  override def getProgress: Float = {
      Option(this.lastCommunication) match {
        case Some(communication) =>
          communication.getDoubleCounter(CommunicationTool.PERCENTAGE).floatValue()
        case _ => 0f
      }
  }

  /**
   * Progress info
   *
   * @return
   */
override def getProgressInfo: Array[JobProgressInfo] = {
    // datax does not have failed task
    var totalTask: Long = 0
    var finishTask: Long = 0
    Option(this.lastCommunication) match {
      case Some(communication) =>
        // Just statistics the total job
        finishTask = communication.getLongCounter(CommunicationTool.STAGE)
        // reverse calculate
        val percentage = communication.getDoubleCounter(CommunicationTool.PERCENTAGE)
        totalTask = (finishTask.toDouble / percentage).toInt
      case _ =>
    }
    Array(JobProgressInfo(this.jobId, totalTask.toInt, (totalTask - finishTask).toInt, 0, finishTask.toInt))
}

  /**
   * Metrics info
   *
   * @return
   */
  override def getMetrics: util.Map[String, Any] = {
    // Convert the whole counter in communication
    Option(this.lastCommunication) match {
      case Some(communication) =>
        val counter = communication.getCounter
        counter.asInstanceOf[util.Map[String, Any]]
      case _ => new util.HashMap[String, Any]()
    }
  }


}
