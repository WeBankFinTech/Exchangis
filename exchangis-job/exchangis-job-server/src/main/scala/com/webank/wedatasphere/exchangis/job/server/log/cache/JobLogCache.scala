package com.webank.wedatasphere.exchangis.job.server.log.cache

import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread

trait JobLogCache{
    def cacheLog(tenancy: String, jobExecId: String, log: String)
}
object JobLogCache{
    def getCache: JobLogCache = {
        null
    }
}
abstract class AbstractJobLogCache extends JobLogCache with SchedulerThread{

    /**
     * Start entrance
     */
    override def start(): Unit = ???

    /**
     * Stop entrance
     */
    override def stop(): Unit = ???

    /**
     * Name of observer
     *
     * @return
     */
    override def getName: String = ???

    override def run(): Unit = ???
}
