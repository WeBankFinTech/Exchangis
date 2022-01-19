package com.webank.wedatasphere.exchangis.job.server.log.serivce

import java.util

import com.webank.wedatasphere.exchangis.job.server.log.JobLogService

class DefaultJobLogService extends JobLogService{
  override def logsFromPage(): Unit = ???

  override def appendLog(tenancy: String, logPath: String, logs: util.List[String]): Unit = ???

  override def appendLog(logPath: String, logs: util.List[String]): Unit = ???
}
