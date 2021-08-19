package com.webank.wedatasphere.exchangis.server.boot

import com.webank.wedatasphere.linkis.DataWorkCloudApplication
import com.webank.wedatasphere.linkis.common.utils.Utils

object ExchangisServerApplication {

  val userName:String = System.getProperty("username.name")
  val hostName:String = Utils.getComputerName

  def main(args: Array[String]): Unit = {

    DataWorkCloudApplication.main(args)
  }
}
