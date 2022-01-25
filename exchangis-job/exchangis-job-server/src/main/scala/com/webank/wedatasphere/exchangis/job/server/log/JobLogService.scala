package com.webank.wedatasphere.exchangis.job.server.log
import java.util

import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache


/**
 * Job Log service
 */
trait JobLogService{

  def getOrCreateLogCache: JobLogCache[String]

  def logsFromPage() : Unit

  def appendLog(tenancy: String, jobExecId: String, logs: util.List[String]): Unit

  def appendLog(jobExecId: String, logs: util.List[String]): Unit
}
