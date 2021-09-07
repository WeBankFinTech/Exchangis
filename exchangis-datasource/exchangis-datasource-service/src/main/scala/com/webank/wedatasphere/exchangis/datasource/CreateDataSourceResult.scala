package com.webank.wedatasphere.exchangis.datasource

import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/datasourcemanager/info/json")
class CreateDataSourceResult extends scala.AnyRef with DWSResult {

  @BeanProperty var type_data: java.util.Map[String, Any] = _

  def getCreatedId: Long = {
    type_data.get("insert_id").asInstanceOf[Long]
  }

}
