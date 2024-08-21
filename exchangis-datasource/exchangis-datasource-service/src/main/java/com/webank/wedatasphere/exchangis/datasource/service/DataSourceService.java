package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceDetail;
import com.webank.wedatasphere.exchangis.datasource.remote.DataSourceDbTableColumn;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceItem;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceTypeDefinition;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVo;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.datasourcemanager.common.domain.DataSource;

import java.util.List;
import java.util.Map;

/**
 * Data source service api
 * TODO define all inf in <em>ExchangisDataSourceService</em>
 */
public interface DataSourceService {
    /**
     * Create data source
     * @param operator operator
     * @param vo create vo
     * @return data source version parameters
     */
    Map<String, Object> create(String operator, DataSourceCreateVo vo) throws ExchangisDataSourceException;

    /**
     * Update data source
     * @param operator operator
     * @param vo create vo
     * @return data source version parameters
     * @throws ExchangisDataSourceException e
     */
    Map<String, Object> update(String operator, Long id, DataSourceCreateVo vo) throws ExchangisDataSourceException;

    /**
     * Delete data source
     * @param operator operator
     * @param id data source id
     * @return id
     * @throws ExchangisDataSourceException e
     */
    Long delete(String operator, Long id) throws ExchangisDataSourceException;
    /**
     * List data source types
     * @param operator operator
     * @param engineType engine type
     * @param direct direct
     * @param sourceType source type
     * @return
     */
    List<ExchangisDataSourceTypeDefinition> listDataSourceTypes(String operator,
                                                                String engineType, String direct, String sourceType) throws ExchangisDataSourceException;

    /**
     * Query data sources
     * @param operator operator
     * @return page result
     */
    PageResult<ExchangisDataSourceItem> queryDataSources(String operator, DataSourceQueryVo vo);

    /**
     * List data sources
     * @param operator operator
     * @param typeName type name
     * @param typeId type id
     * @param page page num
     * @param pageSize page size
     * @return
     */
    List<ExchangisDataSourceItem> listDataSources(String operator,
                                                  String typeName, Long typeId,
                                                  Integer page, Integer pageSize);

    /**
     * Get data source detail
     * @param operator operator
     * @param id
     * @param versionId
     * @return
     */
    ExchangisDataSourceDetail getDataSource(String operator, Long id, String versionId);
    /**
     * Get databases from data source
     * @param operator operator
     * @param type type
     * @param id data source id
     * @return databases
     */
    List<String> getDatabases(String operator, String type, Long id);

    /**
     * Get tables in database from data source
     * @param operator operator
     * @param type type
     * @param id data source id
     * @param database database name
     * @return tables
     */
    List<String> getTables(String operator, String type, Long id, String database);

    /**
     * Get table fields or columns
     * @param operator operator
     * @param type type
     * @param id data source id
     * @param database database name
     * @param table table name
     * @return columns
     */
    List<DataSourceDbTableColumn> getTableFields(String operator, String type,
                                                 Long id, String database, String table);

    /**
     * Copy data source
     * @param operator operate user
     * @param sourceName source name
     * @param newName new name
     */
    void copyDataSource(String operator,
                        String sourceName, String newName) throws ErrorException;

    /**
     * Recycle data source
     */
    void recycleDataSource(String userName, String handover) throws ExchangisDataSourceException;

}
