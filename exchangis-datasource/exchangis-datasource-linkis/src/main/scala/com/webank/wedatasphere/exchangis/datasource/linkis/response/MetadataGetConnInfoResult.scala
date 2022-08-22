package com.webank.wedatasphere.exchangis.datasource.linkis.response

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty
import java.util
/**
 * Result of get connection info
 */
@DWSHttpMessageResult("/api/rest_j/v\\d+/metadataQuery/getConnectionInfo")
class MetadataGetConnInfoResult extends DWSResult{
  @BeanProperty var info: util.Map[String, String] = _
}
