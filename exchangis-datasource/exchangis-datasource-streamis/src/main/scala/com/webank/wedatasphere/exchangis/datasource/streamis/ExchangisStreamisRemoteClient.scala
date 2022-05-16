package com.webank.wedatasphere.exchangis.datasource.streamis

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceConfiguration
import java.lang
import java.util.concurrent.TimeUnit

import org.apache.linkis.datasource.client.impl.{LinkisDataSourceRemoteClient, LinkisMetaDataRemoteClient}
import org.apache.linkis.datasource.client.request.{GetAllDataSourceTypesAction, GetConnectParamsByDataSourceIdAction, MetadataGetColumnsAction, MetadataGetDatabasesAction, MetadataGetTablesAction, QueryDataSourceAction}
import org.apache.linkis.datasource.client.response.{GetConnectParamsByDataSourceIdResult, MetadataGetColumnsResult, MetadataGetDatabasesResult, MetadataGetTablesResult, QueryDataSourceResult}
import org.apache.linkis.datasourcemanager.common.domain.{DataSource, DataSourceType}
import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import org.apache.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}

object ExchangisStreamisRemoteClient {
  //Linkis Datasource Client Config
  val serverUrl: String = ExchangisDataSourceConfiguration.SERVER_URL.getValue
  val connectionTimeout: lang.Long = ExchangisDataSourceConfiguration.CONNECTION_TIMEOUT.getValue
  val discoveryEnabled: lang.Boolean = ExchangisDataSourceConfiguration.DISCOVERY_ENABLED.getValue
  val discoveryFrequencyPeriod: lang.Long = ExchangisDataSourceConfiguration.DISCOVERY_FREQUENCY_PERIOD.getValue
  val loadbalancerEnabled: lang.Boolean = ExchangisDataSourceConfiguration.LOAD_BALANCER_ENABLED.getValue
  val maxConnectionSize: Integer = ExchangisDataSourceConfiguration.MAX_CONNECTION_SIZE.getValue
  val retryEnabled: lang.Boolean = ExchangisDataSourceConfiguration.RETRY_ENABLED.getValue
  val readTimeout: lang.Long = ExchangisDataSourceConfiguration.READ_TIMEOUT.getValue
  val authTokenKey: String = ExchangisDataSourceConfiguration.AUTHTOKEN_KEY.getValue
  val authTokenValue: String = ExchangisDataSourceConfiguration.AUTHTOKEN_VALUE.getValue
  val dwsVersion: String = ExchangisDataSourceConfiguration.DWS_VERSION.getValue


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
    .addServerUrl(serverUrl)
    .connectionTimeout(connectionTimeout)
    .discoveryEnabled(discoveryEnabled)
    .discoveryFrequency(discoveryFrequencyPeriod, TimeUnit.MINUTES)
    .loadbalancerEnabled(loadbalancerEnabled)
    .maxConnectionSize(maxConnectionSize)
    .retryEnabled(retryEnabled)
    .readTimeout(readTimeout)
    .setAuthenticationStrategy(new StaticAuthenticationStrategy())
    .setAuthTokenKey(authTokenKey)
    .setAuthTokenValue(authTokenValue)
    .setDWSVersion(dwsVersion)
    .build()

  val dataSourceClient = new LinkisDataSourceRemoteClient(clientConfig)

  val metaDataClient = new LinkisMetaDataRemoteClient(clientConfig)

  def getStreamisDataSourceRemoteClient: LinkisDataSourceRemoteClient = {
    dataSourceClient
  }

  def getStreamisMetadataRemoteClient: LinkisMetaDataRemoteClient = {
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
