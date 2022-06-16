package com.webank.wedatasphere.exchangis.job.server.log.client

import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder
import org.springframework.context.ApplicationContext

class JobLogHttpClient {

}
object JobLogHttpClient{
  def main(args: Array[String]): Unit = {
    val spring: ApplicationContext = SpringContextHolder.getApplicationContext
    spring match {
      case e: ApplicationContext => print("hello world")
      case _ => print("none")
    }
  }
}