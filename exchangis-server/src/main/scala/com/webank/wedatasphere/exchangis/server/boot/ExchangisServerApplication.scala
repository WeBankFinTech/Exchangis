package com.webank.wedatasphere.exchangis.server.boot


import com.webank.wedatasphere.linkis.DataWorkCloudApplication
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.server.utils.LinkisMainHelper
import org.apache.commons.lang.{ArrayUtils, StringUtils}
import org.slf4j.{Logger, LoggerFactory}
class ExchangisServerApplication{

}
object ExchangisServerApplication {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[ExchangisServerApplication])

  private val MAIN_PROGRAM_NAME: String = "exchangis"

  def main(args: Array[String]): Unit = {
    LOG.info("Start to run ExchangisServerApplication")
    ApplicationUtils.savePidAndRun{
      val userName = Utils.getJvmUser
      val hostName = Utils.getComputerName
      System.setProperty("hostName", hostName)
      System.setProperty("userName", userName)
      val serviceName = Option(System.getProperty(LinkisMainHelper.SERVER_NAME_KEY)).getOrElse(MAIN_PROGRAM_NAME)
      System.setProperty("spring.application.name", serviceName)
      LinkisMainHelper.formatPropertyFiles(MAIN_PROGRAM_NAME, serviceName)
      val allArgs: Array[String] = ArrayUtils.addAll(args.asInstanceOf[Array[Object]],
        LinkisMainHelper.getExtraSpringOptions(MAIN_PROGRAM_NAME).asInstanceOf[Array[Object]]).asInstanceOf[Array[String]]
      val argsString = StringUtils.join(allArgs.asInstanceOf[Array[Object]], '\n')
      val startLog = String.format(s"Ready to start $serviceName with args: $argsString.")
      LOG.info(startLog)
      DataWorkCloudApplication.main(allArgs)
    }
  }

}
