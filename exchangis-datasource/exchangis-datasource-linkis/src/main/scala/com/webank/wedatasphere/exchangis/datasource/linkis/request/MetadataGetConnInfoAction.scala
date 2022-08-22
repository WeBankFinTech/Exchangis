package com.webank.wedatasphere.exchangis.datasource.linkis.request

import org.apache.linkis.datasource.client.request.DataSourceAction
import org.apache.linkis.httpclient.request.GetAction
import java.util
import scala.collection.JavaConverters._
/**
 * Get connection info action
 */
class MetadataGetConnInfoAction(dataSourceName: String, system: String, query: util.Map[String, Any]) extends GetAction with DataSourceAction{

  setParameter("dataSourceName", dataSourceName);
  setParameter("system", system);

  Option(query) match {
    case Some(queryParams) =>
      queryParams.asScala.foreach(param => setParameter(param._1, param._2))
    case _ =>
  }
  private var user: String = _

  override def suffixURLs: Array[String] = Array("metadataQuery", "getConnectionInfo")

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user
}
