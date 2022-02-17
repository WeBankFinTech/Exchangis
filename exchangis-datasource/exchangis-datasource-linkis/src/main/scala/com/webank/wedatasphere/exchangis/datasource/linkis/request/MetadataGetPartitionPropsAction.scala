package com.webank.wedatasphere.exchangis.datasource.linkis.request

import org.apache.linkis.datasource.client.config.DatasourceClientConfig.METADATA_SERVICE_MODULE
import org.apache.linkis.datasource.client.request.DataSourceAction
import org.apache.linkis.httpclient.request.GetAction

class MetadataGetPartitionPropsAction extends GetAction with DataSourceAction{
  /**
   * Data source id
   */
  private var dataSourceId: Long = _

  /**
   * Database
   */
  private var database: String = _

  /**
   * Table
   */
  private var table: String = _

  /**
   * Partition
   */
  private var partition: String = _

  override def suffixURLs: Array[String] = Array(METADATA_SERVICE_MODULE.getValue, "props", dataSourceId.toString, "db", database, "table", table, "partition", partition)

  private var user: String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

  /**
   * Just use the constructor instead of builder
   * @param dataSourceId data source id
   * @param database database
   * @param table table
   * @param partition partition
   * @param system system
   */
  def this(dataSourceId: Long, database: String, table: String, partition: String,
           system: String){
    this()
    this.dataSourceId = dataSourceId
    this.database = database
    this.table = table
    this.partition = partition
    setParameter("system", system)
  }
}
