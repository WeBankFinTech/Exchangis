package com.webank.wedatasphere.exchangis.datasource

import org.apache.linkis.datasource.client.config.DatasourceClientConfig.DATA_SOURCE_SERVICE_MODULE
import org.apache.linkis.datasource.client.exception.DataSourceClientBuilderException
import org.apache.linkis.datasource.client.request.DataSourceAction
import org.apache.linkis.httpclient.request.GetAction


class GetDataSourceInfoByIdAndVersionIdAction extends GetAction with DataSourceAction {
  private var user: String = _
  private var dataSourceId: Long = _
  private var versionId: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  override def suffixURLs: Array[String] = Array(DATA_SOURCE_SERVICE_MODULE.getValue, "info", dataSourceId.toString, versionId)
}


object GetDataSourceInfoByIdAndVersionIdAction {
  def builder(): Builder = new Builder

  class Builder private[GetDataSourceInfoByIdAndVersionIdAction]() {
    private var dataSourceId: Long = _
    private var versionId: String = _
    private var system:String = _
    private var user: String = _

    def setUser(user: String): Builder = {
      this.user = user
      this
    }

    def setDataSourceId(dataSourceId: Long): Builder = {
      this.dataSourceId = dataSourceId
      this
    }

    def setSystem(system: String): Builder = {
      this.system = system
      this
    }

    def setVersionId(versionId: String): Builder = {
      this.versionId = versionId
      this
    }

    def build(): GetDataSourceInfoByIdAndVersionIdAction = {
      if(dataSourceId == null) throw new DataSourceClientBuilderException("dataSourceId is needed!")
      if(versionId == null) throw new DataSourceClientBuilderException("versionId is needed!")
      if(system == null) throw new DataSourceClientBuilderException("system is needed!")
      if(user == null) throw new DataSourceClientBuilderException("user is needed!")

      val GetDataSourceInfoByIdAndVersionIdAction = new GetDataSourceInfoByIdAndVersionIdAction
      GetDataSourceInfoByIdAndVersionIdAction.dataSourceId = this.dataSourceId
      GetDataSourceInfoByIdAndVersionIdAction.setParameter("system", system)
      GetDataSourceInfoByIdAndVersionIdAction.versionId = this.versionId
      GetDataSourceInfoByIdAndVersionIdAction.setUser(user)
      GetDataSourceInfoByIdAndVersionIdAction
    }
  }
}