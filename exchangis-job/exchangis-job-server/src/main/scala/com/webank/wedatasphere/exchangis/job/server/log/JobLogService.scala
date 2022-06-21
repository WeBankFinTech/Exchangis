package com.webank.wedatasphere.exchangis.job.server.log
import java.util

import com.webank.wedatasphere.exchangis.job.log.{LogQuery, LogResult}
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache


/**
 * Job Log service
 */
trait JobLogService{

  def getOrCreateLogCache(jobExecId: String): JobLogCache[String]

  def logsFromPage(jobExecId: String, logQuery: LogQuery): LogResult

  def logsFromPageAndPath(logPath: String, logQuery: LogQuery): LogResult

  def appendLog(tenancy: String, jobExecId: String, logs: util.List[String]): Unit

  def appendLog(jobExecId: String, logs: util.List[String]): Unit
}
