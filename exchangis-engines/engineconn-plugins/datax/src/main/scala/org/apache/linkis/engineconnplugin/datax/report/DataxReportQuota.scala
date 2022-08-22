package org.apache.linkis.engineconnplugin.datax.report

import org.apache.linkis.protocol.engine.JobProgressInfo
import java.util
/**
 * Quota interface
 */
trait DataxReportQuota {

  /**
   * Progress value
   * @return
   */
  def getProgress: Float

  /**
   * Progress info
   * @return
   */
  def getProgressInfo: Array[JobProgressInfo]

  /**
   * Metrics info
   * @return
   */
  def getMetrics: util.Map[String, Any]

}
