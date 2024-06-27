package com.webank.wedatasphere.exchangis.datasource.linkis

import com.webank.wedatasphere.exchangis.common.linkis.client.{ClientConfiguration, ExchangisHttpClient}
import com.webank.wedatasphere.exchangis.common.linkis.client.config.ExchangisClientConfig
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceConfiguration
import com.webank.wedatasphere.exchangis.datasource.core.exception.{ExchangisDataSourceException, ExchangisDataSourceExceptionCode}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.datasource.client.config.DatasourceClientConfig.DATA_SOURCE_SERVICE_CLIENT_NAME
import org.apache.linkis.datasource.client.impl.{LinkisDataSourceRemoteClient, LinkisMetaDataRemoteClient}
import org.apache.linkis.datasource.client.request._
import org.apache.linkis.datasource.client.response._
import org.apache.linkis.datasourcemanager.common.domain.{DataSource, DataSourceType}
import org.apache.linkis.httpclient.dws.DWSHttpClient


object ExchangisLinkisRemoteClient {

  //Linkis Datasource Client Config
  val clientConfig: ExchangisClientConfig = ExchangisClientConfig.newBuilder
    .addServerUrl(ExchangisDataSourceConfiguration.SERVER_URL.getValue)
    .setAuthTokenValue(ExchangisDataSourceConfiguration.AUTH_TOKEN_VALUE.getValue)
    .setDWSVersion(ExchangisDataSourceConfiguration.DWS_VERSION.getValue)
    .build()

  /**
   * Data source client
   */
  val dataSourceClient = new ExchangisDataSourceClient(clientConfig, null)

  /**
   * Meta data client
   */
  val metaDataClient = new ExchangisMetadataClient(clientConfig)

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

/**
 * Exchangis data source client
 * @param clientConfig client config
 * @param clientName client name
 */
class ExchangisDataSourceClient(clientConfig: ExchangisClientConfig, clientName: String) extends LinkisDataSourceRemoteClient(clientConfig, clientName){

  protected override val dwsHttpClient: DWSHttpClient = {
    val client = if (StringUtils.isEmpty(clientName))  DATA_SOURCE_SERVICE_CLIENT_NAME.getValue else clientName
    Option(clientConfig) match {
      case Some(config) => new ExchangisHttpClient(config, client)
      case _ => throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode, "Linkis client config cannot be null")
    }
  }
}

/**
 * Exchangis meta data client
 * @param clientConfig client config
 */
class ExchangisMetadataClient(clientConfig: ExchangisClientConfig) extends LinkisMetaDataRemoteClient(clientConfig){
  protected override val dwsHttpClient: DWSHttpClient = new ExchangisHttpClient(clientConfig, "MetaData-Client")
}
