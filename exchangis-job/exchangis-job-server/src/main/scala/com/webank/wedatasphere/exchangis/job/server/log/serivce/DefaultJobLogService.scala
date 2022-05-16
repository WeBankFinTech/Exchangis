package com.webank.wedatasphere.exchangis.job.server.log.serivce

import java.util

import com.webank.wedatasphere.exchangis.job.log.{LogQuery, LogResult}
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService
import com.webank.wedatasphere.exchangis.job.server.log.cache.JobLogCache

class DefaultJobLogService extends JobLogService{
  override def getOrCreateLogCache(jobExecId: String): JobLogCache[String] = ???

  override def logsFromPage(jobExecId: String, logQuery: LogQuery): LogResult = ???

  override def appendLog(tenancy: String, jobExecId: String, logs: util.List[String]): Unit = ???

  override def appendLog(jobExecId: String, logs: util.List[String]): Unit = ???

  override def logsFromPageAndPath(logPath: String, logQuery: LogQuery): LogResult = ???
}
