package com.webank.wedatasphere.exchangis.datasource.linkis.response

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty

@DWSHttpMessageResult("/api/rest_j/v\\d+/data-source-manager/op/connect/json")
class ParamsTestConnectResult extends DWSResult{
  @BeanProperty var ok: Boolean = _

}
