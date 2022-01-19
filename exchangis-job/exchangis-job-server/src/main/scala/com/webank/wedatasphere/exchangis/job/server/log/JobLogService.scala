package com.webank.wedatasphere.exchangis.job.server.log
import java.util

/**
 * Job Log service
 */
trait JobLogService{

  def logsFromPage() : Unit

  def appendLog(tenancy: String, jobExecId: String, logs: util.List[String]): Unit

  def appendLog(jobExecId: String, logs: util.List[String]): Unit
}
