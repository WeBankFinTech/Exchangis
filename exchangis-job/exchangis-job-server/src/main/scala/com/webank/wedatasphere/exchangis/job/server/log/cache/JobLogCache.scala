package com.webank.wedatasphere.exchangis.job.server.log.cache

import java.util
import java.util.concurrent.{ArrayBlockingQueue, Future}

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.SchedulerThread
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.scheduler.Scheduler
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.LOG_OP_ERROR
import com.webank.wedatasphere.exchangis.job.server.log.JobLogService
import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder
trait JobLogCache[V] extends Logging {
    def cacheLog(log: V)

    def flushCache: Unit
}

object JobLogCacheUtils{
    lazy val jobLogService: JobLogService = SpringContextHolder.getBean(classOf[JobLogService])
    def flush(jobExecId: String, isEnd: Boolean = false): Unit ={
        jobLogService match {
            case service: JobLogService => service.getOrCreateLogCache(jobExecId) match {
                case cache: JobLogCache[String] => cache.flushCache
            }
            case _ =>
        }
    }
}
abstract class AbstractJobLogCache[V](scheduler: Scheduler, maxSize: Int = 100, flushInterval: Int = 2000) extends JobLogCache[V] with SchedulerThread{

    var lastFlush: Long = -1L

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
            Utils.tryAndError{
                flushCache
                lastFlush = System.currentTimeMillis
            }
            Utils.tryAndError(Thread.sleep(flushInterval))
        }
        info(s"Thread: [ ${Thread.currentThread.getName} ] is stopped.")
    }

    override def cacheLog(log: V): Unit = {
        val element: Any = getCacheQueueElement(log)
        if (!cacheQueue.offer(element)) {
            warn("The cache queue is full, should flush the cache immediately")
            flushCache
        } else if (lastFlush + flushInterval < System.currentTimeMillis){
            trace("The cache has reached the time to be flush")
            flushCache
        } else onCache(log)
    }

    protected def onCache(log: V): Unit = {
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

    def getCacheQueueElement(log: V): Any = {
        log
    }
}
