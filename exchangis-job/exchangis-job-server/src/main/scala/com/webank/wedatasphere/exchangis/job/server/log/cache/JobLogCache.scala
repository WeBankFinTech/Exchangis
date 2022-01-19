package com.webank.wedatasphere.exchangis.job.server.log.cache

import java.util
import java.util.concurrent.{ArrayBlockingQueue, Future}

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.scheduler.Scheduler
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR
trait JobLogCache[K, V] extends Logging {
    def cacheLog(cacheId: K, log: V)

    def initCacheId(tenancy: String, jobExecId: String): K

    def flushCache(): Unit
}

abstract class AbstractJobLogCache[K, V](scheduler: Scheduler, maxSize: Int = 1000, flushInterval: Int = 100) extends JobLogCache[K, V] with SchedulerThread{

    var cacheQueue: util.Queue[Any] = new ArrayBlockingQueue[Any](maxSize)

    var isShutdown: Boolean = false

    var flushFuture: Future[_] = _
    /**
     * Start entrance
     */
    override def start(): Unit = {
        this.flushFuture = scheduler match {
            case scheduler: Scheduler => scheduler.getSchedulerContext.getOrCreateConsumerManager.getOrCreateExecutorService.submit(this)
            case _ => throw new ExchangisJobServerException(LOG_OP_ERROR.getCode, s"TaskScheduler cannot be empty, please set it before starting the [$getName]")
        }
    }

    override def run(): Unit = {
        Thread.currentThread.setName(s"JobLogCache-Refresher-$getName")
        info(s"Thread: [ ${Thread.currentThread.getName} ] is started.")
        while (!isShutdown){
            Utils.tryAndError(flushCache())
            Utils.tryAndError(Thread.sleep(flushInterval))
        }
        info(s"Thread: [ ${Thread.currentThread.getName} ] is stopped.")
    }

    override def cacheLog(cacheId: K, log: V): Unit = {
        val element: Any = getCacheQueueElement(cacheId, log)
        if (!cacheQueue.offer(element)) {
            warn("The cache queue is full, should flush the cache immediately")
            flushCache(cacheId)
        } else onCache(cacheId, log)
    }

    protected def onCache(cacheId: K, log: V): Unit = {
        // Do nothing
    }
    /**
     * Stop entrance
     */
    override def stop(): Unit = {
        Option(this.flushFuture).foreach( future => {
            this.isShutdown = true
            future.cancel(true)
        })
    }

    /**
     * Name
     *
     * @return
     */
    override def getName: String = "Default"

    /**
     * Sync to flush special cache
     * @param cacheId id
     */
    def flushCache(cacheId: K): Unit

    def getCacheQueueElement(cacheId: K, log: V): Any
}
