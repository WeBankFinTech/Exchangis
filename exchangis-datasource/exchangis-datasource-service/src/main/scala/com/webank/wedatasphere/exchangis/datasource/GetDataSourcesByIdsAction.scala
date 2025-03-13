package com.webank.wedatasphere.exchangis.datasource

import org.apache.linkis.datasourcemanager.common.domain.DataSource
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-source-manager/info/ids")
class GetDataSourcesByIdsAction extends DWSResult{
  @BeanProperty var info: java.util.Map[String, Any] = _

  def getDataSource: DataSource = {
    val str = DWSHttpClient.jacksonJson.writeValueAsString(info)
    DWSHttpClient.jacksonJson.readValue(str, classOf[DataSource])
  }
}
