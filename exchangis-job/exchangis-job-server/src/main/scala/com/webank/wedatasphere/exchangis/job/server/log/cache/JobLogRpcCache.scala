//package com.webank.wedatasphere.exchangis.job.server.log.cache
//
//import java.util
//
//import org.apache.linkis.scheduler.Scheduler
//import java.util.concurrent.{ArrayBlockingQueue, ConcurrentHashMap}
//import java.util.concurrent.locks.ReentrantLock
//
//abstract class JobLogRpcCache(scheduler: Scheduler, maxSize: Int = 1000, flushInterval: Int = 100) extends AbstractJobLogCache[RpcCacheId, String](scheduler,maxSize,flushInterval) {
//  val rpcCacheSplits: ConcurrentHashMap[String, ServerLogSplit] = new ConcurrentHashMap[String, ServerLogSplit]
//  /**
//   * Sync to flush special cache
//   * @param cacheId id
//   */
//  override def flushCache(cacheId: RpcCacheId): Unit = {
//  }
//
//  override def flushCache(): Unit = {
//
//  }
//
//  override def onCache(cacheId: RpcCacheId, log: String): Unit = {
//      rpcCacheSplits.computeIfAbsent(s"${cacheId.protocol}://${cacheId.address}:${cacheId.port}", (serverUrl: String) => {
//        new ServerLogSplit()
//      }) match {
//        case split: ServerLogSplit => split.queue.add(log)
//        case _ =>
//      }
//  }
//  override def getCacheQueueElement(cacheId: RpcCacheId, log: String): Any = {
//    // Just use the Int token as element
//      1
//  }
//
//  def rpcCall(serverUrl: String, jobExecId: String, tenancy: String, logs: util.List[String])
//
//  class ServerLogSplit{
//     val splitLock: ReentrantLock = new ReentrantLock
//     val queue: util.Queue[String] = new ArrayBlockingQueue[String](maxSize)
//  }
//}
//object JobLogRpcCache{
//  implicit def scalaFunctionToJava[From, To](function: (From) => To): java.util.function.Function[From, To] = {
//    new java.util.function.Function[From, To] {
//      override def apply(input: From): To = function(input)
//    }
//  }
//}
//case class RpcCacheId(address: String, port: String, jobExecId: String, tenancy: String = "default", protocol: String = "http")
//
