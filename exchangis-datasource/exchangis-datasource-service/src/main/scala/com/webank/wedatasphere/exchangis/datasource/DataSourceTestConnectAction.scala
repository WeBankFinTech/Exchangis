package com.webank.wedatasphere.exchangis.datasource

import com.webank.wedatasphere.linkis.datasource.client.request.DataSourceAction
import com.webank.wedatasphere.linkis.httpclient.request.PutAction

class DataSourceTestConnectAction(val id: Long, val version: Long) extends PutAction with DataSourceAction {
  private var user: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array("datasourcemanager", id+"", version+"", "op", "connect")

  override def getRequestPayload: String = ""
}


