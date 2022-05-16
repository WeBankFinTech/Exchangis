package com.webank.wedatasphere.exchangis.datasource.linkis.request

import org.apache.linkis.datasource.client.config.DatasourceClientConfig.DATA_SOURCE_SERVICE_MODULE
import org.apache.linkis.datasource.client.request.DataSourceAction
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.request.POSTAction

import java.util
import scala.collection.JavaConverters.mapAsScalaMapConverter

/**
  * Connect test for the data source params
 */
class ParamsTestConnectAction extends POSTAction with DataSourceAction{

  private var user: String = _

  override def getRequestPayload: String = DWSHttpClient.jacksonJson.writeValueAsString(getRequestPayloads)

  override def suffixURLs: Array[String] = Array(DATA_SOURCE_SERVICE_MODULE.getValue, "op", "connect", "json")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  /**
   *
   * @param dataSource data source map
   * @param user user
   */
  def this(dataSource: util.Map[String, Any], user: String){
    this()
    dataSource.asScala.foreach{
      case (key, value) =>
        this.addRequestPayload(key, value)
    }
    this.user = user
  }
}
