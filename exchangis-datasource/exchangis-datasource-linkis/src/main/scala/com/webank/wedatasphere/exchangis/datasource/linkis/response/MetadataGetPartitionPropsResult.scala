package com.webank.wedatasphere.exchangis.datasource.linkis.response

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import org.apache.linkis.httpclient.dws.response.DWSResult

import scala.beans.BeanProperty
import java.util

@DWSHttpMessageResult("/api/rest_j/v\\d+/metadatamanager/props/(\\S+)/db/(\\S+)/table/(\\S+)/partition/(\\S+)")
class MetadataGetPartitionPropsResult extends DWSResult{
  @BeanProperty var props: util.Map[String, String] = _
}
