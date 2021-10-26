package com.webank.wedatasphere.exchangis

import com.webank.wedatasphere.linkis.datasource.client.impl.{LinkisDataSourceRemoteClient, LinkisMetaDataRemoteClient}
import com.webank.wedatasphere.linkis.datasource.client.request._
import com.webank.wedatasphere.linkis.datasource.client.response._
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.{DataSource, DataSourceType}
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.{StaticAuthenticationStrategy, TokenAuthenticationStrategy}
import com.webank.wedatasphere.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}

import java.lang
import java.util.concurrent.TimeUnit

object TestExchangisLinkisRemoteClient {
  //Linkis Datasource Client Config
  //  val clientConfig = DWSClientConfigBuilder.newBuilder()
  //    .addServerUrl(serverUrl)
  //    .connectionTimeout(connectionTimeout)
  //    .discoveryEnabled(discoveryEnabled)
  //    .discoveryFrequency(1,TimeUnit.MINUTES)
  //    .loadbalancerEnabled(loadbalancerEnabled)
  //    .maxConnectionSize(maxConnectionSize)
  //    .retryEnabled(retryEnabled)
  //    .readTimeout(readTimeout)
  //    .setAuthenticationStrategy(new StaticAuthenticationStrategy())
  //    .setAuthTokenKey(authTokenKey)
  //    .setAuthTokenValue(authTokenValue)
  //    .setDWSVersion(dwsVersion)
  //    .build()

  val clientConfig: DWSClientConfig = DWSClientConfigBuilder.newBuilder()
    .addServerUrl("http://121.36.12.247:8088")
    .connectionTimeout(30000L)
    .discoveryEnabled(true)
    .discoveryFrequency(1L, TimeUnit.MINUTES)
    .loadbalancerEnabled(true)
    .maxConnectionSize(5)
    .retryEnabled(false)
    .readTimeout(30000L)
    .setAuthenticationStrategy(new StaticAuthenticationStrategy())
    .setAuthTokenKey("hdfs")
    .setAuthTokenValue("hdfs")
    .setDWSVersion("v1")
    .build()

  val dataSourceClient = new LinkisDataSourceRemoteClient(clientConfig)

  val metaDataClient = new LinkisMetaDataRemoteClient(clientConfig)

  def getLinkisDataSourceRemoteClient: LinkisDataSourceRemoteClient = {
    dataSourceClient
  }

  def getLinkisMetadataRemoteClient: LinkisMetaDataRemoteClient = {
    metaDataClient
  }

  def close(): Unit = {
    dataSourceClient.close()
    metaDataClient.close()
  }

  def queryDataSource(linkisDatasourceName: String): QueryDataSourceResult = {
    dataSourceClient.queryDataSource(QueryDataSourceAction.builder()
      .setSystem("")
      .setName(linkisDatasourceName)
      .setTypeId(1)
      .setIdentifies("")
      .setCurrentPage(1)
      .setUser("hadoop")
      .setPageSize(1).build()
    )
  }

//  def createDataSource() = {
//    dataSourceClient.execute().asInstanceOf[]
//  }

  /**
   * get datasourceConnect information
   *
   * @param dataSourceId id
   * @param system       dssSystem
   * @param user         username
   * @return
   */
  def queryConnectParams(dataSourceId: Long, system: String, user: String): GetConnectParamsByDataSourceIdResult = {
    dataSourceClient.getConnectParams(GetConnectParamsByDataSourceIdAction.builder()
      .setDataSourceId(dataSourceId)
      .setSystem(system)
      .setUser(user)
      .build()
    )
  }

  /**
   * get all DataSourceTypes
   *
   * @param user user
   * @return
   */
  def queryDataSourceTypes(user: String): java.util.List[DataSourceType] = {
    dataSourceClient.getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
      .setUser(user)
      .build()
    ).getAllDataSourceType
  }


  def queryClusterByDataSourceType(system: String, name: String, typeId: Long, user: String): java.util.List[DataSource] = {
    dataSourceClient.queryDataSource(QueryDataSourceAction.builder()
      .setSystem(system)
      .setName(name)
      .setTypeId(typeId)
      .setIdentifies("")
      .setCurrentPage(1)
      .setPageSize(10)
      .setUser(user)
      .build()
    ).getAllDataSource
  }


  /**
   * get DataBases list
   *
   * @param system
   * @param dataSourceId
   * @param user
   * @return list<String>
   */
  def queryDataBasesByCuster(system: String, dataSourceId: Long, user: String): MetadataGetDatabasesResult = {
    metaDataClient.getDatabases(MetadataGetDatabasesAction.builder()
      .setSystem(system)
      .setDataSourceId(dataSourceId)
      .setUser(user)
      .build()
    )
  }

  def queryTablesByDataBase(system: String, dataSourceId: Long, dataBase: String, user: String): MetadataGetTablesResult = {
    metaDataClient.getTables(MetadataGetTablesAction.builder()
      .setSystem(system)
      .setDataSourceId(dataSourceId)
      .setDatabase(dataBase)
      .setUser(user)
      .build()
    )
  }

  def queryColumnsByTable(system: String, dataSourceId: Long, dataBase: String, table: String, user: String): MetadataGetColumnsResult = {
    metaDataClient.getColumns(MetadataGetColumnsAction.builder()
      .setSystem(system)
      .setDataSourceId(dataSourceId)
      .setDatabase(dataBase)
      .setTable(table)
      .setUser(user)
      .build()
    )
  }


}
